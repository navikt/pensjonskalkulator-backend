package no.nav.pensjon.kalkulator.simulering

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import java.time.LocalDate

class SimuleringServiceTest : FunSpec({

    val pidGetter = mockk<PidGetter>().apply { every { pid() } returns pid }

    test("simulerAlderspensjon uses specified inntekt and sivilstand") {
        val incomingSpec = impersonalSimuleringSpec(REGISTRERT_INNTEKT, Sivilstand.UOPPGITT)
        val simuleringClient = arrangeSimuleringClient(incomingSpec)
        val inntektService = mockk<InntektService>()
        val personClient = mockk<PersonClient>()
        val service = SimuleringService(simuleringClient, inntektService, personClient, pidGetter)

        val response = service.simulerPersonligAlderspensjon(incomingSpec)

        response.alderspensjon[0].beloep shouldBe 123456
        verify { inntektService wasNot Called }
        verify { personClient wasNot Called }

    }

    test("simulerAlderspensjon obtains registrert inntekt and sivilstand when not specified") {
        val incomingSpec = impersonalSimuleringSpec(forventetInntekt = null, sivilstand = null)
        val simuleringClient = arrangeSimuleringClient(incomingSpec)
        val inntektService = arrangeInntekt()
        val personClient = arrangePerson()
        val service = SimuleringService(simuleringClient, inntektService, personClient, pidGetter)

        val response = service.simulerPersonligAlderspensjon(incomingSpec)

        response.alderspensjon[0].beloep shouldBe PENSJONSBELOEP
        verify(exactly = 1) { inntektService.sistePensjonsgivendeInntekt() }
        verify(exactly = 1) { personClient.fetchPerson(pid, fetchFulltNavn = false) }
    }
})

private const val REGISTRERT_INNTEKT = 543210
private const val PENSJONSBELOEP = 123456
private val personalSpec = PersonalSimuleringSpec(pid, Sivilstand.UOPPGITT, REGISTRERT_INNTEKT)

private val inntekt = Inntekt(
    type = Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT,
    aar = 2023,
    beloep = REGISTRERT_INNTEKT.toBigDecimal()
)

private val simuleringResult =
    SimuleringResult(
        alderspensjon = listOf(
            SimulertAlderspensjon(
                alder = 67,
                beloep = PENSJONSBELOEP,
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
        ),
        alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(
            gradertUttak = null,
            heltUttak = 0
        ),
        afpPrivat = emptyList(),
        afpOffentlig = emptyList(),
        vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
        harForLiteTrygdetid = false,
        trygdetid = 0,
        opptjeningGrunnlagListe = emptyList()
    )

private fun impersonalSimuleringSpec(forventetInntekt: Int?, sivilstand: Sivilstand?) =
    ImpersonalSimuleringSpec(
        simuleringType = SimuleringType.ALDERSPENSJON,
        sivilstand = sivilstand,
        eps = Eps(harInntektOver2G = false, harPensjon = false),
        forventetAarligInntektFoerUttak = forventetInntekt,
        heltUttak = HeltUttak(
            uttakFomAlder = Alder(67, 1),
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

private fun arrangeInntekt(): InntektService =
    mockk<InntektService>().apply {
        every { sistePensjonsgivendeInntekt() } returns inntekt
    }

private fun arrangePerson(): PersonClient =
    mockk<PersonClient>().apply {
        every { fetchPerson(pid, fetchFulltNavn = false) } returns person()
    }

private fun arrangeSimuleringClient(incomingSpec: ImpersonalSimuleringSpec): SimuleringClient =
    mockk<SimuleringClient>().apply {
        every { simulerPersonligAlderspensjon(incomingSpec, personalSpec) } returns simuleringResult
    }
