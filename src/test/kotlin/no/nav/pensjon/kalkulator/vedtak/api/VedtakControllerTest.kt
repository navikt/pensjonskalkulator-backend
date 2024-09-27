package no.nav.pensjon.kalkulator.vedtak.api

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakService
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakDetaljer
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
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

    @MockBean
    private lateinit var service: LoependeVedtakService

    @MockBean
    private lateinit var traceAid: TraceAid

    @MockBean
    private lateinit var pidExtractor: PidExtractor

    @MockBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockBean
    private lateinit var auditor: Auditor

    @Test
    fun `hent loepende vedtak V1`() {
        `when`(service.hentLoependeVedtak()).thenReturn(
            LoependeVedtak(
                alderspensjon = LoependeVedtakDetaljer(
                    grad = 1,
                    fom = LocalDate.parse("2020-12-01")
                ),
                ufoeretrygd = LoependeVedtakDetaljer(
                    grad = 2,
                    fom = LocalDate.parse("2021-12-01")
                ),
                afpPrivat = LoependeVedtakDetaljer(
                    grad = 3,
                    fom = LocalDate.parse("2022-12-01")
                ),
                afpOffentlig = LoependeVedtakDetaljer(
                    grad = 4,
                    fom = LocalDate.parse("2023-12-01")
                ),
            )
        )

        val res = mvc.get(URL).andReturn()

        assertEquals(200, res.response.status)
        assertEquals(RESPONSE_BODY_ALLE_MULIGE_VEDTAK, res.response.contentAsString)
    }

    @Test
    fun `hent loepende vedtak V1 ingen vedtak`() {
        `when`(service.hentLoependeVedtak()).thenReturn(
            LoependeVedtak(
                alderspensjon = null,
                ufoeretrygd = null,
                afpPrivat = null,
                afpOffentlig = null,
            )
        )

        val res = mvc.get(URL).andReturn()

        assertEquals(200, res.response.status)
        assertEquals(RESPONSE_BODY_INGEN_VEDTAK, res.response.contentAsString)
    }

    private companion object {
        private const val URL = "/api/v1/vedtak/loepende-vedtak"

        @Language("json")
        private const val RESPONSE_BODY_INGEN_VEDTAK = """{"alderspensjon":{"loepende":false,"grad":0},"ufoeretrygd":{"loepende":false,"grad":0},"afpPrivat":{"loepende":false,"grad":0},"afpOffentlig":{"loepende":false,"grad":0}}"""

        @Language("json")
        private const val RESPONSE_BODY_ALLE_MULIGE_VEDTAK = """{"alderspensjon":{"loepende":true,"grad":1,"fom":"2020-12-01"},"ufoeretrygd":{"loepende":true,"grad":2,"fom":"2021-12-01"},"afpPrivat":{"loepende":true,"grad":3,"fom":"2022-12-01"},"afpOffentlig":{"loepende":true,"grad":4,"fom":"2023-12-01"}}"""
    }
}