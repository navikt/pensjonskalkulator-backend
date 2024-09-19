package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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

    @BeforeEach
    fun initialize() {
        service = UttaksalderService(simuleringService, inntektService, personService, pidGetter)
        `when`(pidGetter.pid()).thenReturn(pid)
    }

    @Test
    fun `finnTidligsteUttaksalder uses properties from impersonal spec`() {
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
        arrangeSimulering(impersonalSpec, true)

        service.finnTidligsteUttaksalder(impersonalSpec)

        verify(personService, never()).getPerson()
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
        arrangeSimulering(impersonalSpec, false, person)

        service.finnTidligsteUttaksalder(impersonalSpec)

        verify(personService, times(1)).getPerson()
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
        arrangeSimulering(impersonalSpec, true, person)

        service.finnTidligsteUttaksalder(impersonalSpec)

        val simuleringSpec = ImpersonalSimuleringSpec(
            simuleringType = impersonalSpec.simuleringType,
            sivilstand = Sivilstand.SAMBOER,
            eps = Eps(harInntektOver2G = true, harPensjon = false), // since samboer
            forventetAarligInntektFoerUttak = 1,
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
        verify(simuleringService, times(1)).simulerAlderspensjon(simuleringSpec)
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
        arrangeSimulering(impersonalSpec, true)

        service.finnTidligsteUttaksalder(impersonalSpec)

        val simuleringSpec = ImpersonalSimuleringSpec(
            simuleringType = impersonalSpec.simuleringType,
            sivilstand = Sivilstand.REGISTRERT_PARTNER,
            eps = Eps(
                harInntektOver2G = true, // since specified sivilstand is 'registrert partner'
                harPensjon = false
            ),
            forventetAarligInntektFoerUttak = 1,
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
        verify(simuleringService, times(1)).simulerAlderspensjon(simuleringSpec)
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
        arrangeSimulering(impersonalSpec, false, person)

        service.finnTidligsteUttaksalder(impersonalSpec)

        val simuleringSpec = ImpersonalSimuleringSpec(
            simuleringType = impersonalSpec.simuleringType,
            sivilstand = Sivilstand.GIFT,
            eps = Eps(harInntektOver2G = false, harPensjon = false), // not overridden (despite sivilstand 'gift')
            forventetAarligInntektFoerUttak = 543210,
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
        verify(simuleringService, times(1)).simulerAlderspensjon(simuleringSpec)
    }

    private fun arrangeSimulering(
        spec: ImpersonalUttaksalderSpec,
        epsHarInntektOver2G: Boolean,
        person: Person? = null,
        angittInntekt: Inntekt? = null
    ) {
        `when`(
            simuleringService.simulerAlderspensjon(
                ImpersonalSimuleringSpec(
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
            )
        ).thenReturn(
            SimuleringResult(
                alderspensjon = emptyList(),
                afpPrivat = emptyList(),
                afpOffentlig = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                harForLiteTrygdetid = false,
                opptjeningGrunnlagListe = emptyList()
            )
        )
    }

    private companion object {
        private val inntekt = Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, 2023, BigDecimal("543210"))
    }
}
