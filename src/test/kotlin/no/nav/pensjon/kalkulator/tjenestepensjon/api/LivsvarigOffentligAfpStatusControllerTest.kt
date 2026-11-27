package no.nav.pensjon.kalkulator.tjenestepensjon.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.verify
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjon.TjenestepensjonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.json.JsonCompareMode.STRICT
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.server.ResponseStatusException

@WebMvcTest(LivsvarigOffentligAfpStatusController::class)
@Import(MockSecurityConfiguration::class)
class LivsvarigOffentligAfpStatusControllerTest : FunSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var tjenestepensjonService: TjenestepensjonService

    @MockkBean(relaxed = true)
    private lateinit var traceAid: TraceAid

    @MockkBean
    private lateinit var pidExtractor: PidExtractor

    @MockkBean
    private lateinit var adresseService: FortroligAdresseService

    @MockkBean
    private lateinit var auditor: Auditor

    init {
        beforeSpec {
            every { pidExtractor.pid() } returns pid
            every { adresseService.adressebeskyttelseGradering(any()) } returns AdressebeskyttelseGradering.UGRADERT
            every { auditor.audit(any(), any()) } returns Unit
            every { traceAid.callId() } returns "test-call-id"
        }

        beforeTest {
            clearMocks(tjenestepensjonService, traceAid, answers = false)
        }

        afterTest {
            verify(exactly = 1) { tjenestepensjonService.harAfpOffentligLivsvarig() }
            verify(exactly = 1) { traceAid.begin() }
            verify(exactly = 1) { traceAid.end() }
        }

        listOf(true, false).forEach { harAfp ->
            test("return 200 with harLivsvarigOffentligAfp=$harAfp") {
                every { tjenestepensjonService.harAfpOffentligLivsvarig() } returns harAfp

                mvc.perform(get(URL).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""{"harLivsvarigOffentligAfp":$harAfp}""", STRICT))
            }
        }

        test("return 503 when the external service is unavailable") {
            every { tjenestepensjonService.harAfpOffentligLivsvarig() } throws
                    EgressException("TP-registeret er utilgjengelig", statusCode = HttpStatus.SERVICE_UNAVAILABLE)

            val result = mvc.perform(get(URL).with(csrf()))
                .andExpect(status().isServiceUnavailable())
                .andReturn()

            result.resolvedException.shouldBeInstanceOf<ResponseStatusException>().reason shouldBe
                    "Call ID: test-call-id | Error: feil ved henting av livsvarig offentlig afp status | Details: TP-registeret er utilgjengelig"
        }
    }

    private companion object {
        private const val URL = "/api/v1/har-livsvarig-offentlig-afp"
    }
}
