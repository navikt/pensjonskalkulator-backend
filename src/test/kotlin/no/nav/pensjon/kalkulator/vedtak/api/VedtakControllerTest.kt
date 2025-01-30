package no.nav.pensjon.kalkulator.vedtak.api

import kotlinx.coroutines.test.runTest
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.vedtak.*
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@WebMvcTest(VedtakController::class)
@Import(MockSecurityConfiguration::class)
class VedtakControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockitoBean
    private lateinit var loependeVedtakService: LoependeVedtakService

    @MockitoBean
    private lateinit var service: VedtakMedUtbetalingService

    @MockitoBean
    private lateinit var traceAid: TraceAid

    @MockitoBean
    private lateinit var pidExtractor: PidExtractor

    @MockitoBean
    private lateinit var fortroligAdresseService: FortroligAdresseService

    @MockitoBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockitoBean
    private lateinit var auditor: Auditor

    @Test
    fun `hent loepende vedtak V1`() {
        `when`(loependeVedtakService.hentLoependeVedtak()).thenReturn(
            LoependeVedtak(
                alderspensjon = LoependeAlderspensjonDetaljer(
                    grad = 1,
                    fom = LocalDate.parse("2020-12-01"),
                    sivilstand = Sivilstand.GIFT
                ),
                fremtidigLoependeVedtakAp = FremtidigAlderspensjonDetaljer(
                    grad = 10,
                    fom = LocalDate.parse("2021-12-01"),
                    sivilstand = Sivilstand.SKILT
                ),
                ufoeretrygd = LoependeUfoeretrygdDetaljer(
                    grad = 2,
                    fom = LocalDate.parse("2021-12-01")
                ),
                afpPrivat = LoependeVedtakDetaljer(
                    fom = LocalDate.parse("2022-12-01")
                ),
                afpOffentlig = LoependeVedtakDetaljer(
                    fom = LocalDate.parse("2023-12-01")
                ),
            )
        )

        val res = mvc.get(URL_V1).andReturn()

        assertEquals(200, res.response.status)
        assertEquals(RESPONSE_BODY_ALLE_MULIGE_VEDTAK_V1, res.response.contentAsString)
    }

    @Test
    fun `hent loepende vedtak V1 ingen vedtak`() {
        `when`(loependeVedtakService.hentLoependeVedtak()).thenReturn(
            LoependeVedtak(
                alderspensjon = null,
                fremtidigLoependeVedtakAp = FremtidigAlderspensjonDetaljer(
                    grad = 10,
                    fom = LocalDate.parse("2021-12-01"),
                    sivilstand = Sivilstand.SKILT
                ),
                ufoeretrygd = null,
                afpPrivat = null,
                afpOffentlig = null,
            )
        )

        val res = mvc.get(URL_V1).andReturn()

        assertEquals(200, res.response.status)
        assertEquals(RESPONSE_BODY_INGEN_VEDTAK_V1, res.response.contentAsString)
    }

    @Test
    fun `hent loepende vedtak V2`() = runTest {
        `when`(service.hentVedtakMedUtbetaling()).thenReturn(
            LoependeVedtak(
                alderspensjon = LoependeAlderspensjonDetaljer(
                    grad = 1,
                    fom = LocalDate.parse("2020-12-01"),
                    sivilstand = Sivilstand.GIFT
                ),
                fremtidigLoependeVedtakAp = FremtidigAlderspensjonDetaljer(
                    grad = 10,
                    fom = LocalDate.parse("2021-12-01"),
                    sivilstand = Sivilstand.SKILT
                ),
                ufoeretrygd = LoependeUfoeretrygdDetaljer(
                    grad = 2,
                    fom = LocalDate.parse("2021-12-01")
                ),
                afpPrivat = LoependeVedtakDetaljer(
                    fom = LocalDate.parse("2022-12-01")
                ),
                afpOffentlig = LoependeVedtakDetaljer(
                    fom = LocalDate.parse("2023-12-01")
                ),
            )
        )

        val res = mvc.get(URL_V2).asyncDispatch().andReturn()

        assertEquals(200, res.response.status)
        assertEquals(RESPONSE_BODY_ALLE_MULIGE_VEDTAK_V2, res.response.contentAsString)
    }

    @Test
    fun `hent loepende vedtak V2 ingen vedtak`() = runTest {
        `when`(service.hentVedtakMedUtbetaling()).thenReturn(
            LoependeVedtak(
                alderspensjon = null,
                fremtidigLoependeVedtakAp = FremtidigAlderspensjonDetaljer(
                    grad = 10,
                    fom = LocalDate.parse("2021-12-01"),
                    sivilstand = Sivilstand.SKILT
                ),
                ufoeretrygd = null,
                afpPrivat = null,
                afpOffentlig = null,
            )
        )

        val res = mvc.get(URL_V2).asyncDispatch().andReturn()

        assertEquals(200, res.response.status)
        assertEquals(RESPONSE_BODY_INGEN_VEDTAK_V2, res.response.contentAsString)
    }

    @Test
    fun `hent loepende vedtak V3`() = runTest {
        `when`(service.hentVedtakMedUtbetaling()).thenReturn(
            LoependeVedtak(
                alderspensjon = LoependeAlderspensjonDetaljer(
                    grad = 1,
                    fom = LocalDate.parse("2020-12-01"),
                    sivilstand = Sivilstand.GIFT
                ),
                fremtidigLoependeVedtakAp = FremtidigAlderspensjonDetaljer(
                    grad = 10,
                    fom = LocalDate.parse("2021-12-01"),
                    sivilstand = Sivilstand.SKILT
                ),
                ufoeretrygd = LoependeUfoeretrygdDetaljer(
                    grad = 2,
                    fom = LocalDate.parse("2021-12-01")
                ),
                afpPrivat = LoependeVedtakDetaljer(
                    fom = LocalDate.parse("2022-12-01")
                ),
                afpOffentlig = LoependeVedtakDetaljer(
                    fom = LocalDate.parse("2023-12-01")
                ),
            )
        )

        val res = mvc.get(URL_V3).asyncDispatch().andReturn()

        assertEquals(200, res.response.status)
        assertEquals(RESPONSE_BODY_ALLE_MULIGE_VEDTAK_V3, res.response.contentAsString)
    }

    @Test
    fun `hent loepende vedtak V3 ingen vedtak`() = runTest {
        `when`(service.hentVedtakMedUtbetaling()).thenReturn(
            LoependeVedtak(
                alderspensjon = null,
                fremtidigLoependeVedtakAp = FremtidigAlderspensjonDetaljer(
                    grad = 10,
                    fom = LocalDate.parse("2021-12-01"),
                    sivilstand = Sivilstand.SKILT
                ),
                ufoeretrygd = null,
                afpPrivat = null,
                afpOffentlig = null,
            )
        )

        val res = mvc.get(URL_V3).asyncDispatch().andReturn()

        assertEquals(200, res.response.status)
        assertEquals(RESPONSE_BODY_INGEN_VEDTAK_V3, res.response.contentAsString)
    }


    @Test
    fun `hent loepende vedtak V4`() = runTest {
        `when`(service.hentVedtakMedUtbetaling()).thenReturn(
            LoependeVedtak(
                alderspensjon = LoependeAlderspensjonDetaljer(
                    grad = 1,
                    fom = LocalDate.parse("2020-12-01"),
                    sivilstand = Sivilstand.GIFT
                ),
                fremtidigLoependeVedtakAp = FremtidigAlderspensjonDetaljer(
                    grad = 10,
                    fom = LocalDate.parse("2021-12-01"),
                    sivilstand = Sivilstand.SKILT
                ),
                ufoeretrygd = LoependeUfoeretrygdDetaljer(
                    grad = 2,
                    fom = LocalDate.parse("2021-12-01")
                ),
                afpPrivat = LoependeVedtakDetaljer(
                    fom = LocalDate.parse("2022-12-01")
                ),
                afpOffentlig = LoependeVedtakDetaljer(
                    fom = LocalDate.parse("2023-12-01")
                ),
            )
        )

        val res = mvc.get(URL_V4).asyncDispatch().andReturn()

        assertEquals(200, res.response.status)
        assertEquals(RESPONSE_BODY_ALLE_MULIGE_VEDTAK_V4, res.response.contentAsString)
    }

    @Test
    fun `hent loepende vedtak V4 ingen vedtak`() = runTest {
        `when`(service.hentVedtakMedUtbetaling()).thenReturn(
            LoependeVedtak(
                alderspensjon = null,
                fremtidigLoependeVedtakAp = null,
                ufoeretrygd = null,
                afpPrivat = null,
                afpOffentlig = null,
            )
        )

        val res = mvc.get(URL_V4).asyncDispatch().andReturn()

        assertEquals(200, res.response.status)
        assertEquals(RESPONSE_BODY_INGEN_VEDTAK_V4, res.response.contentAsString)
    }

    private companion object {
        private const val URL_V1 = "/api/v1/vedtak/loepende-vedtak"
        private const val URL_V2 = "/api/v2/vedtak/loepende-vedtak"
        private const val URL_V3 = "/api/v3/vedtak/loepende-vedtak"
        private const val URL_V4 = "/api/v4/vedtak/loepende-vedtak"

        @Language("json")
        private const val RESPONSE_BODY_INGEN_VEDTAK_V1 =
            """{"alderspensjon":{"loepende":false,"grad":0},"ufoeretrygd":{"loepende":false,"grad":0},"afpPrivat":{"loepende":false,"grad":0},"afpOffentlig":{"loepende":false,"grad":0}}"""

        @Language("json")
        private const val RESPONSE_BODY_INGEN_VEDTAK_V2 =
            """{"harFremtidigLoependeVedtak":true,"ufoeretrygd":{"grad":0}}"""

        @Language("json")
        private const val RESPONSE_BODY_INGEN_VEDTAK_V3 =
            """{"harFremtidigLoependeVedtak":true,"ufoeretrygd":{"grad":0}}"""

        @Language("json")
        private const val RESPONSE_BODY_INGEN_VEDTAK_V4 =
            """{"ufoeretrygd":{"grad":0}}"""

        @Language("json")
        private const val RESPONSE_BODY_ALLE_MULIGE_VEDTAK_V1 =
            """{"alderspensjon":{"loepende":true,"grad":1,"fom":"2020-12-01"},"ufoeretrygd":{"loepende":true,"grad":2,"fom":"2021-12-01"},"afpPrivat":{"loepende":true,"grad":100,"fom":"2022-12-01"},"afpOffentlig":{"loepende":true,"grad":100,"fom":"2023-12-01"}}"""

        @Language("json")
        private const val RESPONSE_BODY_ALLE_MULIGE_VEDTAK_V2 =
            """{"alderspensjon":{"grad":1,"fom":"2020-12-01"},"harFremtidigLoependeVedtak":true,"ufoeretrygd":{"grad":2},"afpPrivat":{"fom":"2022-12-01"},"afpOffentlig":{"fom":"2023-12-01"}}"""

        @Language("json")
        private const val RESPONSE_BODY_ALLE_MULIGE_VEDTAK_V3 =
            """{"alderspensjon":{"grad":1,"fom":"2020-12-01","sivilstand":"GIFT"},"harFremtidigLoependeVedtak":true,"ufoeretrygd":{"grad":2},"afpPrivat":{"fom":"2022-12-01"},"afpOffentlig":{"fom":"2023-12-01"}}"""

        @Language("json")
        private const val RESPONSE_BODY_ALLE_MULIGE_VEDTAK_V4 =
            """{"alderspensjon":{"grad":1,"fom":"2020-12-01","sivilstand":"GIFT"},"fremtidigAlderspensjon":{"grad":10,"fom":"2021-12-01"},"ufoeretrygd":{"grad":2},"afpPrivat":{"fom":"2022-12-01"},"afpOffentlig":{"fom":"2023-12-01"}}"""


    }
}
