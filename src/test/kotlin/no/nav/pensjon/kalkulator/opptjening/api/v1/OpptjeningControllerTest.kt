package no.nav.pensjon.kalkulator.opptjening.api.v1

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.opptjening.OpptjeningService
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(OpptjeningController::class)
@Import(MockSecurityConfiguration::class)
class OpptjeningControllerTest : FunSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var opptjeningService: OpptjeningService

    @MockkBean(relaxed = true)
    private lateinit var traceAid: TraceAid

    init {
        beforeSpec {
            every { traceAid.begin() } returns Unit
        }

        test("'opptjening' endpoint version 1") {
            every {
                opptjeningService.opptjening()
            } returns listOf(
                AarligOpptjening(
                    aar = 2021,
                    pensjonsgivendeInntekt = 1,
                    pensjonspoeng = 2.1,
                    omsorgspoeng = 3,
                    maksimalUfoeregrad = 4,
                    pensjonspoengType = "T1",
                    beholdning = 12
                )
            )

            mvc.perform(
                get(URL)
                    .with(csrf())
                    .content("")
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY))
        }
    }

    private companion object {
        private const val URL = "/api/intern/v1/opptjening"

        @Language("json")
        private const val RESPONSE_BODY = """[
    {
      "aar": 2021,
      "pensjonsgivendeInntekt": 1,
      "pensjonspoeng": 2.1,
      "omsorgspoeng": 3,
      "beholdning": 12
    }
]"""
    }
}