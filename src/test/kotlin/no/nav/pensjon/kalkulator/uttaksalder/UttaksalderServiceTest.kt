package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
internal class UttaksalderServiceTest {

    private lateinit var service: UttaksalderService

    @Mock
    private lateinit var simuleringService: SimuleringService

    @Mock
    private lateinit var inntektService: InntektService

    @Mock
    private lateinit var personService: PersonService

    @Mock
    private lateinit var pidGetter: PidGetter

    @Mock
    private lateinit var normalderService: NormertPensjonsalderService

    @Mock
    private lateinit var lavesteUttaksalderService: LavesteUttaksalderService

    @BeforeEach
    fun initialize() {
        service = UttaksalderService(
            simuleringService, inntektService, personService, pidGetter, normalderService, lavesteUttaksalderService
        )

        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(normalderService.nedreAlder(LocalDate.of(1963, 12, 31))).thenReturn(Alder(aar = 62, maaneder = 0))
    }

    @Test
    fun `finnTidligsteUttaksalder uses inntekt from impersonal spec`() {
        `when`(personService.getPerson()).thenReturn(person())

        val impersonalSpec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            sivilstand = Sivilstand.GIFT,
            harEps = true,
            aarligInntektFoerUttak = 100_000,
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
        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.GIFT,
            harEps = true,
            aarligInntektFoerUttak = 100_000
        )
        val simuleringSpec = arrangeSimulering(impersonalSpec, epsHarInntektOver2G = true)
        arrangeLavesteUttaksalder(impersonalSpec, personalSpec, simuleringSpec, harEps = true)

        service.finnTidligsteUttaksalder(impersonalSpec)

        verify(inntektService, never()).sistePensjonsgivendeInntekt()
    }

    @Test
    fun `finnTidligsteUttaksalder obtains inntekt and sivilstand when not specified`() {
        val person = person()
        `when`(inntektService.sistePensjonsgivendeInntekt()).thenReturn(inntekt)
        `when`(personService.getPerson()).thenReturn(person)
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
        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = person.sivilstand, // Sivilstand.SAMBOER
            harEps = false,
            aarligInntektFoerUttak = inntekt.beloep.intValueExact()
        )
        val simuleringSpec = arrangeSimulering(impersonalSpec, epsHarInntektOver2G = false, person)
        arrangeLavesteUttaksalder(impersonalSpec, personalSpec, simuleringSpec, harEps = false)

        service.finnTidligsteUttaksalder(impersonalSpec)

        verify(personService, times(2)).getPerson() // sivilstand + fødselsdato
        verify(inntektService, times(1)).sistePensjonsgivendeInntekt()
    }

    @Test
    fun `when 'har EPS' and sivilstand not specified then service deduces it from person's sivilstand`() {
        val person = person(Sivilstand.SAMBOER) // 'har EPS' is true for samboer
        `when`(personService.getPerson()).thenReturn(person)
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
        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.SAMBOER,
            harEps = true,
            aarligInntektFoerUttak = 1
        )
        val simuleringSpec = arrangeSimulering(impersonalSpec, epsHarInntektOver2G = true, person)
        arrangeLavesteUttaksalder(impersonalSpec, personalSpec, simuleringSpec, harEps = true)

        service.finnTidligsteUttaksalder(impersonalSpec)

        verify(simuleringService, times(1)).simulerPersonligAlderspensjon(simuleringSpec)
    }

    private fun arrangeLavesteUttaksalder(
        impersonalSpec: ImpersonalUttaksalderSpec,
        personalSpec: PersonalUttaksalderSpec,
        simuleringSpec: ImpersonalSimuleringSpec,
        harEps: Boolean
    ) {
        `when`(
            lavesteUttaksalderService.lavesteUttaksalderSimuleringSpec(
                impersonalSpec,
                personalSpec, // = personalSpec(),
                harEps // = true
            )
        ).thenReturn(simuleringSpec)
    }

    @Test
    fun `when 'har EPS' not specified then service deduces it from specified sivilstand`() {
        val person = person()
        `when`(personService.getPerson()).thenReturn(person)
        val impersonalSpec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            sivilstand = Sivilstand.REGISTRERT_PARTNER, // sivilstand specified
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
        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.REGISTRERT_PARTNER,
            harEps = true,
            aarligInntektFoerUttak = 1
        )
        val simuleringSpec = arrangeSimulering(impersonalSpec, epsHarInntektOver2G = true) //, person)
        arrangeLavesteUttaksalder(impersonalSpec, personalSpec, simuleringSpec, harEps = true)

        service.finnTidligsteUttaksalder(impersonalSpec)

        verify(simuleringService, times(1)).simulerPersonligAlderspensjon(simuleringSpec)
    }

    @Test
    fun `when 'har EPS' specified then service does not override it based on sivilstand`() {
        val person = person(Sivilstand.GIFT)
        `when`(inntektService.sistePensjonsgivendeInntekt()).thenReturn(inntekt)
        `when`(personService.getPerson()).thenReturn(person)
        val impersonalSpec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            sivilstand = null,
            harEps = false, // 'har EPS' specified
            aarligInntektFoerUttak = null,
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
        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.GIFT,
            harEps = false,
            aarligInntektFoerUttak = 543210
        )
        val simuleringSpec = arrangeSimulering(impersonalSpec, epsHarInntektOver2G = false)
        arrangeLavesteUttaksalder(impersonalSpec, personalSpec, simuleringSpec, harEps = false)

        service.finnTidligsteUttaksalder(impersonalSpec)

        verify(simuleringService, times(1)).simulerPersonligAlderspensjon(simuleringSpec)
    }

    @Test
    fun `finnTidligsteUttaksalder throws SimuleringException when AFP offentlig is empty`() {
        val person = person()
        `when`(personService.getPerson()).thenReturn(person)
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(normAlderService.nedreAldersgrense()).thenReturn(Alder(aar = 62, maaneder = 0))

        val impersonalSpec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG,
            sivilstand = Sivilstand.GIFT,
            harEps = true,
            aarligInntektFoerUttak = 100_000,
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

        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.GIFT,
            harEps = true,
            aarligInntektFoerUttak = 100_000
        )

        val simuleringSpec = arrangeSimulering(impersonalSpec, epsHarInntektOver2G = true)
        arrangeLavesteUttaksalder(impersonalSpec, personalSpec, simuleringSpec, harEps = true)

        `when`(simuleringService.simulerPersonligAlderspensjon(simuleringSpec)).thenReturn(
            SimuleringResult(
                alderspensjon = emptyList(),
                afpPrivat = emptyList(),
                afpOffentlig = emptyList(), // This should trigger the exception
                vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                harForLiteTrygdetid = false,
                trygdetid = 0,
                opptjeningGrunnlagListe = emptyList()
            )
        )

        val exception = assertThrows<SimuleringException> {
            service.finnTidligsteUttaksalder(impersonalSpec)
        }

        assertEquals(SimuleringStatus.AFP_IKKE_I_VILKAARSPROEVING, exception.status)
    }


    private fun arrangeSimulering(
        uttaksalderSpec: ImpersonalUttaksalderSpec,
        epsHarInntektOver2G: Boolean,
        person: Person? = null,
        angittInntekt: Inntekt? = null
    ): ImpersonalSimuleringSpec {
        val simuleringSpec = simuleringSpec(uttaksalderSpec, epsHarInntektOver2G, person, angittInntekt)

        `when`(
            simuleringService.simulerPersonligAlderspensjon(simuleringSpec)
        ).thenReturn(
            SimuleringResult(
                alderspensjon = emptyList(),
                afpPrivat = emptyList(),
                afpOffentlig = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                harForLiteTrygdetid = false,
                trygdetid = 0,
                opptjeningGrunnlagListe = emptyList()
            )
        )

        return simuleringSpec
    }

    private fun simuleringSpec(
        spec: ImpersonalUttaksalderSpec,
        epsHarInntektOver2G: Boolean,
        person: Person? = null,
        angittInntekt: Inntekt? = null
    ) = ImpersonalSimuleringSpec(
        simuleringType = spec.simuleringType,
        sivilstand = person?.sivilstand ?: spec.sivilstand,
        eps = Eps(harInntektOver2G = epsHarInntektOver2G, harPensjon = false),
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

    private companion object {
        private val inntekt = Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, 2023, BigDecimal("543210"))
    }
}
