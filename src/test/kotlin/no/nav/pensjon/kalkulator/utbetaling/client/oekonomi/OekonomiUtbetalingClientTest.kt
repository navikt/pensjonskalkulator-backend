package no.nav.pensjon.kalkulator.utbetaling.client.oekonomi

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import no.nav.pensjon.kalkulator.utbetaling.Utbetaling
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingServiceTest.Companion.MONTH_END
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingServiceTest.Companion.MONTH_MIDDLE
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingServiceTest.Companion.MONTH_START
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.SokosResponse.UTBETALINGER_RESPONSE
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

class OekonomiUtbetalingClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null

    beforeSpec {
        Arrange.security()
        server = MockWebServer().apply { start() }
        baseUrl = "http://localhost:${server.port}"
    }

    afterSpec {
        server?.shutdown()
    }

    test("hentSisteMaanedsUtbetaling leser inn alle utbetalinger og mapper til riktige verdier") {
        server?.arrangeOkJsonResponse(UTBETALINGER_RESPONSE)

        Arrange.webClientContextRunner().run {
            val client = OekonomiUtbetalingClient(
                baseUrl!!,
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid = mockk<TraceAid>(relaxed = true),
                retryAttempts = "0"
            )

            runBlocking { client.hentSisteMaanedsUtbetaling(pid) } shouldBe
                    listOf(
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

private object SokosResponse {

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
