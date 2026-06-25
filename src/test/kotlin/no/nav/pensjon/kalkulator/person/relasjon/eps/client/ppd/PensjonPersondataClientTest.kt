package no.nav.pensjon.kalkulator.person.relasjon.eps.client.ppd

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Navn
import no.nav.pensjon.kalkulator.person.PersonaliaType
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.person.Tilgangsbegrensning
import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon
import no.nav.pensjon.kalkulator.person.relasjon.RelasjonPersondata
import no.nav.pensjon.kalkulator.person.relasjon.Relasjonstype
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.NyligsteEpsSpec
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeJsonResponse
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

class PensjonPersondataClientTest : ShouldSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid: TraceAid = mockk { every { callId() } returns "id1" }

    fun client(context: BeanFactory) =
        PensjonPersondataClient(
            baseUrl!!,
            webClientBuilder = context.getBean<WebClient.Builder>(),
            cacheManager = mockk(relaxed = true),
            traceAid,
            retryAttempts = "1"
        )

    beforeTest {
        Arrange.security()
        server = MockWebServer().apply { start() }
        baseUrl = "http://localhost:${server.port}"
    }

    afterTest {
        server?.shutdown()
    }

    context("fetchNyligsteEps - suksess") {
        should("returnere familierelasjon med angitte personalia hentet") {
            server?.arrangeOkJsonResponse(OK_RESPONSE_BODY)

            Arrange.webClientContextRunner().run {
                client(context = it).fetchNyligsteEps(
                    spec = NyligsteEpsSpec(
                        soekerPid = pid,
                        sivilstatus = Sivilstatus.SAMBOER,
                        personalia = listOf(PersonaliaType.NAVN)
                    )
                ) shouldBe Familierelasjon(
                    pid = pid,
                    fom = LocalDate.of(2021, 4, 1),
                    relasjonstype = Relasjonstype.SAMBOER,
                    relasjonPersondata = RelasjonPersondata(
                        navn = Navn(fornavn = "F", mellomnavn = "M", etternavn = "E"),
                        foedselsdato = null,
                        doedsdato = null,
                        statsborgerskap = null,
                        tilgangsbegrensning = Tilgangsbegrensning.UNKNOWN
                    )
                )
            }
        }
    }

    context("fetchNyligsteEps - EPS ikke funnet") {
        should("returnere familierelasjon med relasjonstype 'ukjent' og øvrige data udefinert") {
            server?.arrangeJsonResponse(HttpStatus.NOT_FOUND, "{}")

            Arrange.webClientContextRunner().run {
                client(context = it).fetchNyligsteEps(
                    spec = NyligsteEpsSpec(
                        soekerPid = pid,
                        sivilstatus = Sivilstatus.SAMBOER,
                        personalia = listOf(PersonaliaType.NAVN)
                    )
                ) shouldBe Familierelasjon(
                    pid = null,
                    fom = null,
                    relasjonstype = Relasjonstype.UKJENT,
                    relasjonPersondata = null
                )
            }
        }
    }
})

@Language("json")
private const val OK_RESPONSE_BODY = """{
  "pid": "12906498357",
  "fom": "2021-04-01",
  "relasjonstype": "SAMBOER",
  "relasjonPersondata": {
    "navn": {
      "fornavn": "F",
      "mellomnavn": "M",
      "etternavn": "E"
    }
  }
}"""