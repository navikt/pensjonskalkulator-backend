package no.nav.pensjon.kalkulator.simulering

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpService
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.time.TodayProvider
import java.time.LocalDate

class SimuleringServiceTest : ShouldSpec({

    val pidGetter = mockk<PidGetter>().apply { every { pid() } returns pid }

    should("use specified inntekt and sivilstatus") {
        val incomingSpec = impersonalSimuleringSpec(
            forventetInntekt = REGISTRERT_INNTEKT,
            sivilstatus = Sivilstatus.UOPPGITT
        )
        val simuleringClient = arrangeSimuleringClient(incomingSpec)
        val inntektService = mockk<InntektService>()
        val personService = mockk<PersonService>().apply {
            every { getPerson() } returns person(sivilstand = Sivilstand.SKILT)
        }

        val service =
            SimuleringService(
                simuleringClient,
                inntektService,
                personService,
                pidGetter = pidGetter,
                time = arrangeTime(),
                serviceberegnetAfpService = mockk<ServiceberegnetAfpService>()
            )

        val response = service.simulerPersonligAlderspensjon(incomingSpec)

        response.alderspensjon[0].beloep shouldBe 123456
        verify { inntektService wasNot Called }
        verify(exactly = 1) { personService.getPerson() } // for fødselsdato (not sivilstatus)

        verify {
            simuleringClient.simulerPersonligAlderspensjon(
                impersonalSpec = any(),
                personalSpec = match { it.sivilstatus == Sivilstatus.UOPPGITT }, // specified sivilstatus
            )
        }
    }

    should("obtain registrert inntekt and sivilstatus when not specified") {
        val incomingSpec = impersonalSimuleringSpec(forventetInntekt = null, sivilstatus = null)
        val simuleringClient = arrangeSimuleringClient(incomingSpec)
        val inntektService = arrangeInntekt()
        val personService = arrangePerson()

        val service =
            SimuleringService(
                simuleringClient,
                inntektService,
                personService,
                pidGetter,
                time = arrangeTime(),
                serviceberegnetAfpService = mockk<ServiceberegnetAfpService>()
            )

        val response = service.simulerPersonligAlderspensjon(incomingSpec)

        response.alderspensjon[0].beloep shouldBe PENSJONSBELOEP
        verify(exactly = 1) { inntektService.sistePensjonsgivendeInntekt() }
        verify(exactly = 2) { personService.getPerson() } // for sivilstatus and fødselsdato
    }
})

private const val REGISTRERT_INNTEKT = 543210
private const val PENSJONSBELOEP = 123456

private val personalSpec = PersonalSimuleringSpec(
    pid,
    sivilstatus = Sivilstatus.UOPPGITT,
    aarligInntektFoerUttak = REGISTRERT_INNTEKT
)

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
                delingstall = 0.0,
                pensjonBeholdningFoerUttak = 0,
                sluttpoengtall = 0.0,
                poengaarFoer92 = 0,
                poengaarEtter91 = 0,
                forholdstall = 0.0,
                grunnpensjon = 0,
                tilleggspensjon = 0,
                pensjonstillegg = 0,
                skjermingstillegg = 0,
                kapittel19Pensjon = null,
                kapittel20Pensjon = null
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

private fun impersonalSimuleringSpec(forventetInntekt: Int?, sivilstatus: Sivilstatus?) =
    ImpersonalSimuleringSpec(
        simuleringType = SimuleringType.ALDERSPENSJON,
        sivilstatus = sivilstatus,
        eps = EpsSpec(levende = LevendeEps(harInntektOver2G = false, harPensjon = false)),
        forventetAarligInntektFoerUttak = forventetInntekt,
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

private fun arrangeInntekt(): InntektService =
    mockk<InntektService>().apply {
        every { sistePensjonsgivendeInntekt() } returns inntekt
    }

private fun arrangePerson(): PersonService =
    mockk<PersonService>().apply {
        every { getPerson() } returns person()
    }

private fun arrangeSimuleringClient(incomingSpec: ImpersonalSimuleringSpec): SimuleringClient =
    mockk<SimuleringClient>().apply {
        every { simulerPersonligAlderspensjon(incomingSpec, personalSpec) } returns simuleringResult
    }

private fun arrangeTime(): TodayProvider =
    mockk<TodayProvider>().apply {
        every { date() } returns LocalDate.of(2026, 1, 1)
    }
