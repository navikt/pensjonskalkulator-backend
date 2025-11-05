package no.nav.pensjon.kalkulator.vedtak.api

import io.kotest.matchers.shouldBe
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
    fun `hent loepende vedtak V4`() = runTest {
        `when`(service.hentVedtakMedUtbetaling()).thenReturn(
            VedtakSamling(
                loependeAlderspensjon = LoependeAlderspensjon(
                    grad = 1,
                    fom = LocalDate.parse("2020-12-01"),
                    uttaksgradFom = LocalDate.of(2021, 1, 1),
                    sivilstand = Sivilstand.GIFT
                ),
                fremtidigAlderspensjon = FremtidigAlderspensjon(
                    grad = 10,
                    fom = LocalDate.parse("2021-12-01"),
                    sivilstand = Sivilstand.SKILT
                ),
                ufoeretrygd = LoependeUfoeretrygd(grad = 2, fom = LocalDate.parse("2021-12-01")),
                privatAfp = LoependeEntitet(fom = LocalDate.parse("2022-12-01"))
            )
        )

        val result = mvc.get(URL_V4).asyncDispatch().andReturn()

        with(result.response) {
            status shouldBe 200
            contentAsString shouldBe RESPONSE_BODY_ALLE_MULIGE_VEDTAK_V4
        }
    }

    @Test
    fun `hent loepende vedtak V4 ingen vedtak`() = runTest {
        `when`(service.hentVedtakMedUtbetaling()).thenReturn(
            VedtakSamling(
                loependeAlderspensjon = null,
                fremtidigAlderspensjon = null,
                ufoeretrygd = null,
                privatAfp = null
            )
        )

        val result = mvc.get(URL_V4).asyncDispatch().andReturn()

        with(result.response) {
            status shouldBe 200
            contentAsString shouldBe RESPONSE_BODY_INGEN_VEDTAK_V4
        }
    }

    private companion object {
        private const val URL_V4 = "/api/v4/vedtak/loepende-vedtak"

        @Language("json")
        private const val RESPONSE_BODY_INGEN_VEDTAK_V4 =
            """{"harLoependeVedtak":false,"ufoeretrygd":{"grad":0}}"""

        @Language("json")
        private const val RESPONSE_BODY_ALLE_MULIGE_VEDTAK_V4 =
            """{"harLoependeVedtak":false,"alderspensjon":{"grad":1,"fom":"2020-12-01","uttaksgradFom":"2021-01-01","sivilstand":"GIFT"},"fremtidigAlderspensjon":{"grad":10,"fom":"2021-12-01"},"ufoeretrygd":{"grad":2},"afpPrivat":{"fom":"2022-12-01"}}"""
    }
}
