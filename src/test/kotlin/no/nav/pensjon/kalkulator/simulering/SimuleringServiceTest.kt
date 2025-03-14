package no.nav.pensjon.kalkulator.simulering

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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
class SimuleringServiceTest {

    private lateinit var service: SimuleringService

    @Mock
    private lateinit var simuleringClient: SimuleringClient

    @Mock
    private lateinit var inntektService: InntektService

    @Mock
    private lateinit var personClient: PersonClient

    @Mock
    private lateinit var pidGetter: PidGetter

    @BeforeEach
    fun initialize() {
        service = SimuleringService(simuleringClient, inntektService, personClient, pidGetter)
        `when`(pidGetter.pid()).thenReturn(pid)
    }

    @Test
    fun `simulerAlderspensjon uses specified inntekt and sivilstand`() {
        val incomingSpec = impersonalSimuleringSpec(REGISTRERT_INNTEKT, Sivilstand.UOPPGITT)
        `when`(simuleringClient.simulerPersonligAlderspensjon(incomingSpec, personalSpec)).thenReturn(simuleringResult)

        val response = service.simulerPersonligAlderspensjon(incomingSpec)

        assertEquals(123456, response.alderspensjon[0].beloep)
        verifyNoInteractions(inntektService, personClient)
    }

    @Test
    fun `simulerAlderspensjon obtains registrert inntekt and sivilstand when not specified`() {
        val incomingSpec = impersonalSimuleringSpec(null, null)
        `when`(inntektService.sistePensjonsgivendeInntekt()).thenReturn(inntekt)
        `when`(personClient.fetchPerson(pid, fetchFulltNavn = false)).thenReturn(person())
        `when`(simuleringClient.simulerPersonligAlderspensjon(incomingSpec, personalSpec)).thenReturn(simuleringResult)

        val response = service.simulerPersonligAlderspensjon(incomingSpec)

        assertEquals(PENSJONSBELOEP, response.alderspensjon[0].beloep)
        verify(inntektService, times(1)).sistePensjonsgivendeInntekt()
        verify(personClient, times(1)).fetchPerson(pid, fetchFulltNavn = false)
    }

    @Test
    fun `simulerPersonligAlderspensjon throws SimuleringException when AFP Offentlig is empty`() {
        val incomingSpec = impersonalSimuleringSpec(null, null).copy(simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG)
        `when`(inntektService.sistePensjonsgivendeInntekt()).thenReturn(inntekt)
        `when`(personClient.fetchPerson(pid, fetchFulltNavn = false)).thenReturn(person())
        `when`(simuleringClient.simulerPersonligAlderspensjon(incomingSpec, personalSpec)).thenReturn(simuleringResult.copy(afpOffentlig = emptyList()))

        val exception = assertThrows<SimuleringException> {
            service.simulerPersonligAlderspensjon(incomingSpec)
        }

        assertEquals("Henting av AFP Offentlig feilet", exception.message)
    }

    private companion object {
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
                        pensjonBeholdningFoerUttak = 0
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
    }
}
