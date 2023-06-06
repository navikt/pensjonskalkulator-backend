package no.nav.pensjon.kalkulator.avtale.api

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleService
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(PensjonsavtaleController::class)
@Import(MockSecurityConfiguration::class)
class PensjonsavtaleControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var avtaleService: PensjonsavtaleService

    @Test
    fun getAvtaler() {
        `when`(avtaleService.fetchAvtaler()).thenReturn(avtaler())

        mvc.perform(
            get(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY))
    }

    private companion object {

        private const val URL = "/api/pensjonsavtaler"

        @Language("json")
        private const val RESPONSE_BODY = """{
            "avtaler": [
                        {
                            "navn": "avtale1",
                            "fom": "1992-03-04",
                            "tom": "2010-11-12"
                        }
                    ]
        }"""

        private fun avtaler() = Pensjonsavtaler(listOf(pensjonsavtale()))

        private fun pensjonsavtale() = Pensjonsavtale(
            "avtale1",
            LocalDate.of(1992, 3, 4),
            LocalDate.of(2010, 11, 12)
        )
    }
}
