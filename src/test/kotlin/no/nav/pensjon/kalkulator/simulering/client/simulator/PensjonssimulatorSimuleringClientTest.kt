package no.nav.pensjon.kalkulator.simulering.client.simulator

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.simulator.PensjonssimulatorSimuleringClientTestObjects.ALTERNATIV_PENSJON
import no.nav.pensjon.kalkulator.simulering.client.simulator.PensjonssimulatorSimuleringClientTestObjects.EXPECTED_GRADERT_UTTAK_REQUEST_BODY
import no.nav.pensjon.kalkulator.simulering.client.simulator.PensjonssimulatorSimuleringClientTestObjects.PENSJON_MED_LIVSVARIG_OFFENTLIG_AFP
import no.nav.pensjon.kalkulator.simulering.client.simulator.PensjonssimulatorSimuleringClientTestObjects.VILKAAR_IKKE_OPPFYLT_BODY
import no.nav.pensjon.kalkulator.simulering.client.simulator.PensjonssimulatorSimuleringClientTestObjects.impersonalGradertUttakSpec
import no.nav.pensjon.kalkulator.simulering.client.simulator.PensjonssimulatorSimuleringClientTestObjects.impersonalSpec
import no.nav.pensjon.kalkulator.simulering.client.simulator.PensjonssimulatorSimuleringClientTestObjects.personalSpec
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.BeanFactory
import org.springframework.web.reactive.function.client.WebClient
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDate

class PensjonssimulatorSimuleringClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>().apply { every { callId() } returns "id1" }

    fun client(context: BeanFactory) =
        PensjonssimulatorSimuleringClient(
            baseUrl!!,
            webClientBuilder = context.getBean(WebClient.Builder::class.java),
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

    /**
     * NAU = Nærmest Angitt Uttak (alternative simuleringsparametre)
     */
    test("simulerPersonligAlderspensjon der responsen har alternativ NAU-beregning") {
        server?.arrangeOkJsonResponse(ALTERNATIV_PENSJON)

        Arrange.webClientContextRunner().run {

            val response: SimuleringResult =
                client(context = it).simulerPersonligAlderspensjon(
                    impersonalSpec(),
                    personalSpec(Sivilstand.ENKE_ELLER_ENKEMANN)
                )

            response shouldBe SimuleringResult(
                alderspensjon = listOf(
                    alderspensjon(alder = 63, beloep = 13956),
                    alderspensjon(alder = 64, beloep = 83736),
                    alderspensjon(alder = 65, beloep = 153174),
                    alderspensjon(alder = 66, beloep = 222612),
                    alderspensjon(alder = 67, beloep = 222612),
                    alderspensjon(alder = 68, beloep = 222612),
                    alderspensjon(alder = 69, beloep = 222612),
                    alderspensjon(alder = 70, beloep = 222612),
                    alderspensjon(alder = 71, beloep = 222612),
                    alderspensjon(alder = 72, beloep = 222612),
                    alderspensjon(alder = 73, beloep = 222612),
                    alderspensjon(alder = 74, beloep = 222612),
                    alderspensjon(alder = 75, beloep = 222612),
                    alderspensjon(alder = 76, beloep = 222612),
                    alderspensjon(alder = 77, beloep = 222612)
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 13000, heltUttak = 26000),
                pre2025OffentligAfp = null,
                afpPrivat = emptyList(),
                afpOffentlig = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(
                    innvilget = false,
                    alternativ = Alternativ(
                        gradertUttakAlder = Alder(63, 10),
                        uttakGrad = Uttaksgrad.FOERTI_PROSENT,
                        heltUttakAlder = Alder(65, 6)
                    )
                ),
                harForLiteTrygdetid = false,
                trygdetid = 0,
                opptjeningGrunnlagListe = emptyList()
            )
        }
    }

    test("simulerPersonligAlderspensjon sends request body with gradert uttak when specified") {
        server!!.arrangeOkJsonResponse(ALTERNATIV_PENSJON)

        Arrange.webClientContextRunner().run {
            client(context = it).simulerPersonligAlderspensjon(
                impersonalSpec = impersonalGradertUttakSpec(),
                personalSpec = personalSpec(Sivilstand.ENKE_ELLER_ENKEMANN)
            )

            ByteArrayOutputStream().use {
                server.takeRequest().body.copyTo(it)
                it.toString(StandardCharsets.UTF_8) shouldBe EXPECTED_GRADERT_UTTAK_REQUEST_BODY
            }
        }
    }

    test("simulerPersonligAlderspensjon med ikke-oppfylte vilkaar returnerer alternativ") {
        server?.arrangeOkJsonResponse(VILKAAR_IKKE_OPPFYLT_BODY)

        Arrange.webClientContextRunner().run {
            val result = client(context = it).simulerPersonligAlderspensjon(
                impersonalSpec = impersonalGradertUttakSpec(),
                personalSpec = personalSpec(Sivilstand.UGIFT)
            )

            result shouldBe SimuleringResult(
                alderspensjon = emptyList(),
                alderspensjonMaanedsbeloep = null,
                afpPrivat = emptyList(),
                afpOffentlig = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(
                    innvilget = false,
                    alternativ = Alternativ(
                        gradertUttakAlder = null,
                        uttakGrad = Uttaksgrad.HUNDRE_PROSENT,
                        heltUttakAlder = Alder(aar = 67, maaneder = 0)
                    )
                ),
                harForLiteTrygdetid = false,
                trygdetid = 0,
                opptjeningGrunnlagListe = emptyList()
            )
        }
    }

    test("simulerPersonligAlderspensjon med livsvarig offentlig AFP") {
        server?.arrangeOkJsonResponse(PENSJON_MED_LIVSVARIG_OFFENTLIG_AFP)

        Arrange.webClientContextRunner().run {
            val result: SimuleringResult =
                client(context = it).simulerPersonligAlderspensjon(
                    impersonalSpec = impersonalGradertUttakSpec(),
                    personalSpec = personalSpec(Sivilstand.UGIFT)
                )

            result shouldBe SimuleringResult(
                alderspensjon = listOf(
                    alderspensjon(alder = 62, beloep = 222612),
                    alderspensjon(alder = 63, beloep = 222612),
                    alderspensjon(alder = 64, beloep = 222612),
                    alderspensjon(alder = 65, beloep = 222612),
                    alderspensjon(alder = 66, beloep = 222612),
                    alderspensjon(alder = 67, beloep = 222612),
                    alderspensjon(alder = 68, beloep = 222612),
                    alderspensjon(alder = 69, beloep = 222612),
                    alderspensjon(alder = 70, beloep = 222612),
                    alderspensjon(alder = 71, beloep = 222612),
                    alderspensjon(alder = 72, beloep = 222612),
                    alderspensjon(alder = 73, beloep = 222612),
                    alderspensjon(alder = 74, beloep = 222612),
                    alderspensjon(alder = 75, beloep = 222612)
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 13000, heltUttak = 26000),
                pre2025OffentligAfp = null,
                afpPrivat = emptyList(),
                afpOffentlig = listOf(
                    SimulertAfpOffentlig(
                        alder = 62,
                        beloep = 55000,
                        maanedligBeloep = 0
                    ),
                    SimulertAfpOffentlig(
                        alder = 63,
                        beloep = 65000,
                        maanedligBeloep = 0
                    )
                ),
                vilkaarsproeving = Vilkaarsproeving(
                    innvilget = true,
                    alternativ = null
                ),
                harForLiteTrygdetid = false,
                trygdetid = 0,
                opptjeningGrunnlagListe = emptyList()
            )
        }
    }
})

private fun alderspensjon(alder: Int, beloep: Int) =
    SimulertAlderspensjon(
        alder,
        beloep,
        inntektspensjonBeloep = 0,
        garantipensjonBeloep = 0,
        delingstall = 0.0,
        pensjonBeholdningFoerUttak = 0,
        andelsbroekKap19 = 0.0,
        andelsbroekKap20 = 0.0,
        sluttpoengtall = 0.0,
        trygdetidKap19 = 0,
        trygdetidKap20 = 0,
        poengaarFoer92 = 0,
        poengaarEtter91 = 0,
        forholdstall = 0.0,
        grunnpensjon = 0,
        tilleggspensjon = 0,
        pensjonstillegg = 0,
        skjermingstillegg = 0,
        kapittel19Gjenlevendetillegg = 0
    )

private object PensjonssimulatorSimuleringClientTestObjects {

    @Language("json")
    const val VILKAAR_IKKE_OPPFYLT_BODY = """{
    "alderspensjonListe": [],
    "privatAfpListe": [],
    "livsvarigOffentligAfpListe": [],
    "vilkaarsproeving": {
        "vilkaarErOppfylt": false,
        "alternativ": {
            "uttaksgrad": 100,
            "heltUttakAlder": {
                "aar": 67,
                "maaneder": 0
            }
        }
    },
    "trygdetid": 0,
    "opptjeningGrunnlagListe": []
}"""

    @Language("json")
    const val EXPECTED_GRADERT_UTTAK_REQUEST_BODY = """{"simuleringstype":"ALDER","pid":"12906498357","sivilstand":"ENKE","epsHarPensjon":false,"epsHarInntektOver2G":true,"sisteInntekt":123000,"uttaksar":1,"gradertUttak":{"grad":"P_50","uttakFomAlder":{"aar":64,"maaneder":2},"aarligInntekt":12000},"heltUttak":{"uttakFomAlder":{"aar":67,"maaneder":1},"aarligInntekt":0,"inntektTomAlder":{"aar":67,"maaneder":1}},"utenlandsperiodeListe":[{"fom":"1990-01-02","tom":"1999-11-30","land":"AUS","arbeidetUtenlands":true}],"afpOrdning":"AFPKOM"}"""

    @Language("json")
    const val ALTERNATIV_PENSJON = """{
    "alderspensjonListe": [
        {
            "alderAar": 63,
            "beloep": 13956
        },
        {
            "alderAar": 64,
            "beloep": 83736
        },
        {
            "alderAar": 65,
            "beloep": 153174
        },
        {
            "alderAar": 66,
            "beloep": 222612
        },
        {
            "alderAar": 67,
            "beloep": 222612
        },
        {
            "alderAar": 68,
            "beloep": 222612
        },
        {
            "alderAar": 69,
            "beloep": 222612
        },
        {
            "alderAar": 70,
            "beloep": 222612
        },
        {
            "alderAar": 71,
            "beloep": 222612
        },
        {
            "alderAar": 72,
            "beloep": 222612
        },
        {
            "alderAar": 73,
            "beloep": 222612
        },
        {
            "alderAar": 74,
            "beloep": 222612
        },
        {
            "alderAar": 75,
            "beloep": 222612
        },
        {
            "alderAar": 76,
            "beloep": 222612
        },
        {
            "alderAar": 77,
            "beloep": 222612
        }
    ],
    "alderspensjonMaanedsbeloep": {
        "gradertUttakBeloep": 13000,
        "heltUttakBeloep": 26000
    },
    "privatAfpListe": [],
    "livsvarigOffentligAfpListe": [],
    "vilkaarsproeving": {
        "vilkaarErOppfylt": false,
        "alternativ": {
            "gradertUttakAlder": {
                "aar": 63,
                "maaneder": 10
            },
            "uttaksgrad": 40,
            "heltUttakAlder": {
                "aar": 65,
                "maaneder": 6
            }
        }
    }
}"""

    @Language("json")
    const val PENSJON_MED_LIVSVARIG_OFFENTLIG_AFP = """{
    "alderspensjonListe": [
        {
            "alderAar": 62,
            "beloep": 222612
        },
        {
            "alderAar": 63,
            "beloep": 222612
        },
        {
            "alderAar": 64,
            "beloep": 222612
        },
        {
            "alderAar": 65,
            "beloep": 222612
        },
        {
            "alderAar": 66,
            "beloep": 222612
        },
        {
            "alderAar": 67,
            "beloep": 222612
        },
        {
            "alderAar": 68,
            "beloep": 222612
        },
        {
            "alderAar": 69,
            "beloep": 222612
        },
        {
            "alderAar": 70,
            "beloep": 222612
        },
        {
            "alderAar": 71,
            "beloep": 222612
        },
        {
            "alderAar": 72,
            "beloep": 222612
        },
        {
            "alderAar": 73,
            "beloep": 222612
        },
        {
            "alderAar": 74,
            "beloep": 222612
        },
        {
            "alderAar": 75,
            "beloep": 222612
        }
    ],
    "alderspensjonMaanedsbeloep": {
        "gradertUttakBeloep": 13000,
        "heltUttakBeloep": 26000
    },
    "privatAfpListe": [],
    "livsvarigOffentligAfpListe": [
      {
            "alderAar": 62,
            "beloep": 55000
        },
        {
            "alderAar": 63,
            "beloep": 65000
        }
    ],
    "vilkaarsproeving": {
        "vilkaarErOppfylt": true,
        "alternativ": null
    }
}"""

    fun impersonalSpec() =
        ImpersonalSimuleringSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            sivilstand = null,
            eps = Eps(harInntektOver2G = true, harPensjon = false),
            forventetAarligInntektFoerUttak = null,
            heltUttak = HeltUttak(
                uttakFomAlder = Alder(aar = 67, maaneder = 1),
                inntekt = null
            ),
            utenlandsopphold = Utenlandsopphold(
                periodeListe = listOf(
                    Opphold(
                        fom = LocalDate.of(1990, 1, 2),
                        tom = LocalDate.of(1999, 11, 30),
                        land = Land.AUS,
                        arbeidet = true
                    )
                )
            )
        )

    fun impersonalGradertUttakSpec() =
        ImpersonalSimuleringSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            sivilstand = null,
            eps = Eps(harInntektOver2G = true, harPensjon = false),
            forventetAarligInntektFoerUttak = null,
            gradertUttak = GradertUttak(
                grad = Uttaksgrad.FEMTI_PROSENT,
                uttakFomAlder = Alder(aar = 64, maaneder = 2),
                aarligInntekt = 12_000
            ),
            heltUttak = HeltUttak(
                uttakFomAlder = Alder(aar = 67, maaneder = 1),
                inntekt = null
            ),
            utenlandsopphold = Utenlandsopphold(
                periodeListe = listOf(
                    Opphold(
                        fom = LocalDate.of(1990, 1, 2),
                        tom = LocalDate.of(1999, 11, 30),
                        land = Land.AUS,
                        arbeidet = true
                    )
                )
            )
        )

    fun personalSpec(sivilstand: Sivilstand) =
        PersonalSimuleringSpec(
            pid = pid,
            aarligInntektFoerUttak = 123000,
            sivilstand = sivilstand
        )
}
