package no.nav.pensjon.kalkulator.uttaksalder

import io.kotest.core.spec.style.ShouldSpec
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import java.math.BigDecimal
import java.time.LocalDate

class UttaksalderServiceTest : ShouldSpec({

    should("use inntekt from impersonal specification") {
        val impersonalSpec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            sivilstand = Sivilstand.GIFT,
            harEps = true,
            aarligInntektFoerUttak = 100_000,
            heltUttak = HeltUttak(uttakFomAlder = Alder(aar = 67, maaneder = 0), inntekt = null),
            utenlandsperiodeListe = listOf(
                Opphold(
                    fom = LocalDate.of(1990, 1, 2),
                    tom = LocalDate.of(1999, 11, 30),
                    land = Land.AUS,
                    arbeidet = true
                )
            )
        )
        val simuleringSpec = simuleringSpec(impersonalSpec, epsHarInntektOver2G = true)
        val inntektService = mockk<InntektService>()

        UttaksalderService(
            simuleringService = arrangeSimulering(simuleringSpec),
            inntektService,
            personService = mockk(relaxed = true),
            pidGetter = mockk(relaxed = true),
            normalderService = mockk(relaxed = true),
            lavesteUttaksalderService = arrangeLavesteUttaksalder(
                impersonalSpec,
                simuleringSpec,
                harEps = true
            )
        ).finnTidligsteUttaksalder(impersonalSpec)

        verify { inntektService wasNot Called }
    }

    should("obtain inntekt and sivilstand when not specified") {
        val person = person()
        val impersonalSpec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
            sivilstand = null, // sivilstand not specified
            harEps = null,
            aarligInntektFoerUttak = null, // inntekt not specified
            heltUttak = HeltUttak(Alder(67, 0), null),
            utenlandsperiodeListe = listOf(
                Opphold(
                    fom = LocalDate.of(1990, 1, 2),
                    tom = LocalDate.of(1999, 11, 30),
                    land = Land.AUS,
                    arbeidet = true
                )
            )
        )
        val simuleringSpec = simuleringSpec(impersonalSpec, epsHarInntektOver2G = false, person)
        val personService = arrangePerson()
        val inntektService = arrangeInntekt()

        UttaksalderService(
            simuleringService = arrangeSimulering(simuleringSpec),
            inntektService,
            personService,
            pidGetter = mockk(relaxed = true),
            normalderService = arrangeNormalder(),
            lavesteUttaksalderService = arrangeLavesteUttaksalder(
                impersonalSpec,
                simuleringSpec,
                harEps = false
            )
        ).finnTidligsteUttaksalder(impersonalSpec)

        verify(exactly = 2) { personService.getPerson() } // sivilstand + fødselsdato
        verify(exactly = 1) { inntektService.sistePensjonsgivendeInntekt() }
    }

    context("'har EPS' and sivilstand not specified") {
        should("deduce 'har EPS' from person's sivilstand") {
            val person = person(Sivilstand.SAMBOER) // 'har EPS' is true for samboer
            val impersonalSpec = ImpersonalUttaksalderSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                sivilstand = null, // sivilstand not specified
                harEps = null, // 'har EPS' not specified
                aarligInntektFoerUttak = 1,
                heltUttak = HeltUttak(Alder(67, 0), null),
                utenlandsperiodeListe = listOf(
                    Opphold(
                        fom = LocalDate.of(1990, 1, 2),
                        tom = LocalDate.of(1999, 11, 30),
                        land = Land.AUS,
                        arbeidet = true
                    )
                )
            )
            val simuleringSpec = simuleringSpec(impersonalSpec, epsHarInntektOver2G = true, person)
            val simuleringService = arrangeSimulering(simuleringSpec)

            UttaksalderService(
                simuleringService = simuleringService,
                inntektService = mockk(relaxed = true),
                personService = arrangePerson(person),
                pidGetter = mockk(relaxed = true),
                normalderService = arrangeNormalder(),
                lavesteUttaksalderService = arrangeLavesteUttaksalder(
                    impersonalSpec,
                    simuleringSpec,
                    harEps = true
                )
            ).finnTidligsteUttaksalder(impersonalSpec)

            verify(exactly = 1) { simuleringService.simulerPersonligAlderspensjon(simuleringSpec) }
        }
    }

    context("'har EPS' not specified") {
        should("deduce 'har EPS' from specified sivilstand") {
            val uttakSpec = ImpersonalUttaksalderSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                sivilstand = Sivilstand.REGISTRERT_PARTNER, // sivilstand specified
                harEps = null, // 'har EPS' not specified
                aarligInntektFoerUttak = 1,
                heltUttak = HeltUttak(uttakFomAlder = Alder(aar = 67, maaneder = 0), inntekt = null),
                utenlandsperiodeListe = listOf(
                    Opphold(
                        fom = LocalDate.of(1990, 1, 2),
                        tom = LocalDate.of(1999, 11, 30),
                        land = Land.AUS,
                        arbeidet = true
                    )
                )
            )
            val simuleringSpec = simuleringSpec(uttakSpec, epsHarInntektOver2G = true)
            val simuleringService = arrangeSimulering(simuleringSpec)

            UttaksalderService(
                simuleringService = simuleringService,
                inntektService = mockk(),
                personService = arrangePerson(),
                pidGetter = mockk(relaxed = true),
                normalderService = arrangeNormalder(),
                lavesteUttaksalderService = arrangeLavesteUttaksalder(
                    uttakSpec,
                    simuleringSpec,
                    harEps = true
                )
            ).finnTidligsteUttaksalder(uttakSpec)

            verify(exactly = 1) { simuleringService.simulerPersonligAlderspensjon(simuleringSpec) }
        }
    }

    context("'har EPS' specified") {
        should("not override 'har EPS' based on sivilstand") {
            val impersonalSpec = ImpersonalUttaksalderSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                sivilstand = null,
                harEps = false, // 'har EPS' specified (i.e., not null)
                aarligInntektFoerUttak = null,
                heltUttak = HeltUttak(uttakFomAlder = Alder(aar = 67, maaneder = 0), inntekt = null),
                utenlandsperiodeListe = listOf(
                    Opphold(
                        fom = LocalDate.of(1990, 1, 2),
                        tom = LocalDate.of(1999, 11, 30),
                        land = Land.AUS,
                        arbeidet = true
                    )
                )
            )
            val simuleringSpec = simuleringSpec(impersonalSpec, epsHarInntektOver2G = false)
            val simuleringService = arrangeSimulering(simuleringSpec)

            UttaksalderService(
                simuleringService,
                inntektService= arrangeInntekt(),
                personService = arrangePerson(Sivilstand.GIFT),
                pidGetter = mockk(relaxed = true),
                normalderService = arrangeNormalder(),
                lavesteUttaksalderService = arrangeLavesteUttaksalder(
                    impersonalSpec,
                    simuleringSpec,
                    harEps = false
                )
            ).finnTidligsteUttaksalder(impersonalSpec)

            verify(exactly = 1) { simuleringService.simulerPersonligAlderspensjon(simuleringSpec) }
        }
    }
})

private val inntekt =
    Inntekt(
        type = Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT,
        aar = 2023,
        beloep = BigDecimal("543210")
    )

private fun arrangeInntekt(): InntektService =
    mockk<InntektService>().apply {
        every { sistePensjonsgivendeInntekt() } returns inntekt
    }

private fun arrangeLavesteUttaksalder(
    uttakSpec: ImpersonalUttaksalderSpec,
    simuleringSpec: ImpersonalSimuleringSpec,
    harEps: Boolean
): LavesteUttaksalderService =
    mockk<LavesteUttaksalderService>().apply {
        every {
            lavesteUttaksalderSimuleringSpec(
                impersonalSpec = eq(uttakSpec),
                personalSpec = any(),
                harEps = eq(harEps)
            )
        } returns simuleringSpec
    }

private fun arrangeNormalder(): NormertPensjonsalderService =
    mockk<NormertPensjonsalderService>().apply {
        every { nedreAlder(any()) } returns Alder(aar = 62, maaneder = 0)
    }

private fun arrangePerson(sivilstand: Sivilstand = Sivilstand.UOPPGITT): PersonService =
    mockk<PersonService>().apply {
        every { getPerson() } returns person(sivilstand)
    }

private fun arrangePerson(person: Person): PersonService =
    mockk<PersonService>().apply {
        every { getPerson() } returns person
    }

private fun arrangeSimulering(spec: ImpersonalSimuleringSpec): SimuleringService =
    mockk<SimuleringService>().apply {
        every {
            simulerPersonligAlderspensjon(impersonalSpec = spec)
        } returns SimuleringResult(
            alderspensjon = emptyList(),
            afpPrivat = emptyList(),
            afpOffentlig = emptyList(),
            vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
            harForLiteTrygdetid = false,
            trygdetid = 0,
            opptjeningGrunnlagListe = emptyList()
        )
    }

private fun simuleringSpec(
    spec: ImpersonalUttaksalderSpec,
    epsHarInntektOver2G: Boolean,
    person: Person? = null,
    angittInntekt: Inntekt? = null
) = ImpersonalSimuleringSpec(
    simuleringType = spec.simuleringType,
    sivilstand = person?.sivilstand ?: spec.sivilstand,
    eps = EpsSpec(levende = LevendeEps(harInntektOver2G = epsHarInntektOver2G, harPensjon = false)),
    forventetAarligInntektFoerUttak = spec.aarligInntektFoerUttak
        ?: (angittInntekt ?: inntekt).beloep.intValueExact(),
    gradertUttak = null,
    heltUttak = HeltUttak(
        uttakFomAlder = Alder(aar = 62, maaneder = 0),
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
        ),
        antallAar = null
    )
)
