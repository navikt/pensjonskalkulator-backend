package no.nav.pensjon.kalkulator.simulering.api

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@WebMvcTest(SimuleringController::class)
@Import(MockSecurityConfiguration::class)
class SimuleringControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var simuleringClient: SimuleringClient

    @MockBean
    private lateinit var pidGetter: PidGetter

    @Test
    fun simulerAlderspensjon() {
        `when`(simuleringClient.simulerAlderspensjon(anyObject())).thenReturn(simuleringsresultat())
        `when`(pidGetter.pid()).thenReturn(Pid(FNR))

        mvc.perform(
            post(URL)
                .with(csrf())
                .content(REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY))
    }

    private companion object {

        private const val URL = "/api/alderspensjon/simulering"
        private const val FNR = "12906498357" // synthetic fødselsnummer for person born 12 Oct 1964

        private const val REQUEST_BODY = """{
        "simuleringstype": "AP",
        "forventetInntekt": 100000,
        "uttaksgrad": 100,
        "foersteUttaksdato": "2031-11-01",
        "sivilstand": "UGIFT",
        "epsHarInntektOver2G": false
    }"""

        private const val RESPONSE_BODY = """{
        "pensjonsaar": 2033,
        "pensjonsbeloep": 215026,
        "alder": 67
    }"""

        private fun simuleringsresultat() = Simuleringsresultat(2033, BigDecimal("215026"), 67)

        private fun <T> anyObject(): T {
            return Mockito.any()
        }
    }
}
