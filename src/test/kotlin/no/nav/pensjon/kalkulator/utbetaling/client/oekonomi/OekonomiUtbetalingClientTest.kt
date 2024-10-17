package no.nav.pensjon.kalkulator.utbetaling.client.oekonomi

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentertRolle
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.utbetaling.Utbetaling
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingServiceTest.Companion.MONTH_END
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingServiceTest.Companion.MONTH_MIDDLE
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingServiceTest.Companion.MONTH_START
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.SokosResponse.UTBETALINGER_RESPONSE
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.mockito.Mockito.mock
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

class OekonomiUtbetalingClientTest : FunSpec({
    var server: MockWebServer? = null
    var baseUrl: String? = null

    beforeSpec {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext())

        SecurityContextHolder.getContext().authentication = EnrichedAuthentication(
            TestingAuthenticationToken(
                "TEST_USER",
                Jwt("j.w.t", null, null, mapOf(Pair("k", "v")), mapOf(Pair("k", "v")))
            ),
            EgressTokenSuppliersByService(mapOf()),
            RepresentasjonTarget(rolle = RepresentertRolle.SELV)
        )

        server = MockWebServer().also { it.start() }
        baseUrl = "http://localhost:${server!!.port}"
    }

    afterSpec {
        server?.shutdown()
    }

    test("hentSisteMaanedsUtbetaling leser inn alle utbetalinger og mapper til riktige verdier") {
        val contextRunner = ApplicationContextRunner().withConfiguration(
            AutoConfigurations.of(WebClientAutoConfiguration::class.java)
        )
        // Arrange
        server!!.enqueue(
            MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody(UTBETALINGER_RESPONSE)
        )

        contextRunner.run {
            val webClientBuilder = it.getBean(WebClient.Builder::class.java)
            val client = OekonomiUtbetalingClient(baseUrl!!, webClientBuilder, mock(TraceAid::class.java), "1")

            val result: List<Utbetaling> = runBlocking { client.hentSisteMaanedsUtbetaling(pid) }

            result shouldNotBe null
            result.size shouldBe 4
            result shouldBe listOf(
                Utbetaling(
                    utbetalingsdato = null,
                    posteringsdato = LocalDate.of(2024, Month.OCTOBER, MONTH_MIDDLE),
                    beloep = BigDecimal("1000.5"),
                    erUtbetalt = true,
                    gjelderAlderspensjon = true,
                    fom = LocalDate.of(2024, Month.SEPTEMBER, MONTH_START),
                    tom = LocalDate.of(2024, Month.SEPTEMBER, MONTH_END - 1),
                ),
                Utbetaling(
                    utbetalingsdato = LocalDate.of(2024, Month.SEPTEMBER, 20),
                    posteringsdato = LocalDate.of(2024, Month.SEPTEMBER, MONTH_MIDDLE),
                    beloep = BigDecimal("1200.5"),
                    erUtbetalt = true,
                    gjelderAlderspensjon = true,
                    fom = LocalDate.of(2024, Month.AUGUST, MONTH_START),
                    tom = LocalDate.of(2024, Month.AUGUST, MONTH_END),
                ),
                Utbetaling(
                    utbetalingsdato = null,
                    posteringsdato = LocalDate.of(2024, Month.OCTOBER, MONTH_MIDDLE),
                    beloep = BigDecimal("2135692.5"),
                    erUtbetalt = false,
                    gjelderAlderspensjon = false,
                    fom = LocalDate.of(2024, Month.SEPTEMBER, MONTH_START),
                    tom = LocalDate.of(2024, Month.SEPTEMBER, MONTH_END - 1),
                ),
                Utbetaling(
                    utbetalingsdato = null,
                    posteringsdato = LocalDate.of(2024, Month.OCTOBER, MONTH_MIDDLE),
                    beloep = BigDecimal("8651.1"),
                    erUtbetalt = false,
                    gjelderAlderspensjon = false,
                    fom = LocalDate.of(2024, Month.SEPTEMBER, MONTH_START),
                    tom = LocalDate.of(2024, Month.SEPTEMBER, MONTH_END - 1),
                ),
            )
        }
    }
})

object SokosResponse {

    @Language("json")
    const val UTBETALINGER_RESPONSE = """[
{
  "utbetalingsstatus": "18",
  "posteringsdato": "2024-10-15",
  "forfallsdato": "2024-10-20",
  "utbetalingsdato": null,
  "utbetalingNettobeloep": 750.5,
  "ytelseListe": [
    {
      "ytelsestype": "alderspensjon",
      "ytelsesperiode": {
        "fom": "2024-09-01",
        "tom": "2024-09-30"
      },
      "ytelseskomponentersum": 1000.5
    }
  ]
},
{
  "utbetalingsstatus": "18",
  "posteringsdato": "2024-09-15",
  "forfallsdato": "2024-09-20",
  "utbetalingsdato": "2024-09-20",
  "utbetalingNettobeloep": 800.5,
  "ytelseListe": [
    {
      "ytelsestype": "alderspensjon",
      "ytelsesperiode": {
        "fom": "2024-08-01",
        "tom": "2024-08-31"
      },
      "ytelseskomponentersum": 1200.5
    }
  ]
},
{
  "utbetalingsstatus": "16",
  "posteringsdato": "2024-10-15",
  "forfallsdato": "2024-10-20",
  "utbetalingsdato": null,
  "utbetalingNettobeloep": 2139692.5,
  "ytelseListe": [
    {
      "ytelsestype": "lotterigevinst",
      "ytelsesperiode": {
        "fom": "2024-09-01",
        "tom": "2024-09-30"
      },
      "ytelseskomponentersum": 2135692.5
    },
    {
      "ytelsestype": "lommepenger",
      "ytelsesperiode": {
        "fom": "2024-09-01",
        "tom": "2024-09-30"
      },
      "ytelseskomponentersum": 8651.1
    }
  ]
}]"""
}
