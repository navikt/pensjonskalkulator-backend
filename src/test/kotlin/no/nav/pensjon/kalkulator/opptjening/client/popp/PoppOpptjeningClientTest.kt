package no.nav.pensjon.kalkulator.opptjening.client.popp

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.person.Pid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigDecimal

class PoppOpptjeningClientTest : WebClientTest() {

    private lateinit var client: PoppOpptjeningClient

    @BeforeEach
    fun initialize() {
        client = PoppOpptjeningClient(baseUrl(), WebClient.create())
    }

    @Test
    fun `getOpptjeningsgrunnlag returns opptjeningsgrunnlag when OK response`() {
        arrangeSecurityContext()
        arrange(okResponse())

        val response: Opptjeningsgrunnlag = client.getOpptjeningsgrunnlag(Pid("04925398980"))

        val inntekt2017 = response.inntekter.first { it.aar == 2017 }
        val inntekt2018 = response.inntekter.first { it.aar == 2018 }
        assertEquals(BigDecimal("280241"), inntekt2017.beloep)
        assertEquals(Opptjeningstype.OTHER, inntekt2017.type)
        assertEquals(BigDecimal("280242"), inntekt2018.beloep)
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, inntekt2018.type)
    }

    companion object {

        @Language("json")
        private const val RESPONSE_BODY =
            """
             {
                 "opptjeningsGrunnlag": {
                     "fnr": "04925398980",
                     "inntektListe": [
                         {
                             "changeStamp": {
                                 "createdBy": "TESTDATA",
                                 "createdDate": 1586931866460,
                                 "updatedBy": "srvpensjon",
                                 "updatedDate": 1586931866775
                             },
                             "inntektId": 585473583,
                             "fnr": "04925398980",
                             "inntektAr": 2017,
                             "kilde": "PEN",
                             "kommune": "1337",
                             "piMerke": null,
                             "inntektType": "INN_LON",
                             "belop": 280241
                         },
                         {
                             "changeStamp": {
                                 "createdBy": "srvpensjon",
                                 "createdDate": 1586931866782,
                                 "updatedBy": "srvpensjon",
                                 "updatedDate": 1586931866946
                             },
                             "inntektId": 585473584,
                             "fnr": "04925398980",
                             "inntektAr": 2018,
                             "kilde": "POPP",
                             "kommune": null,
                             "piMerke": null,
                             "inntektType": "SUM_PI",
                             "belop": 280242
                         }
                     ],
                     "omsorgListe": [],
                     "dagpengerListe": [],
                     "forstegangstjeneste": null
                 }
             }
             """

        private fun okResponse() = jsonResponse().setBody(RESPONSE_BODY.trimIndent())
    }
}
