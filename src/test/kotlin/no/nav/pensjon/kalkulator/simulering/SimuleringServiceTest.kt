package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
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
        `when`(simuleringClient.simulerAlderspensjon(incomingSpec, personalSpec)).thenReturn(simuleringsresultat)

        val response = service.simulerAlderspensjon(incomingSpec)

        assertEquals(123456, response.alderspensjon[0].beloep)
        verifyNoInteractions(inntektService, personClient)
    }

    @Test
    fun `simulerAlderspensjon obtains registrert inntekt and sivilstand when not specified`() {
        val incomingSpec = impersonalSimuleringSpec(null, null)
        `when`(inntektService.sistePensjonsgivendeInntekt()).thenReturn(inntekt)
        `when`(personClient.fetchPerson(pid)).thenReturn(person())
        `when`(simuleringClient.simulerAlderspensjon(incomingSpec, personalSpec)).thenReturn(simuleringsresultat)

        val response = service.simulerAlderspensjon(incomingSpec)

        assertEquals(PENSJONSBELOEP, response.alderspensjon[0].beloep)
        verify(inntektService, times(1)).sistePensjonsgivendeInntekt()
        verify(personClient, times(1)).fetchPerson(pid)
    }

    private companion object {
        private const val REGISTRERT_INNTEKT = 543210
        private const val PENSJONSBELOEP = 123456
        private val foedselDato = LocalDate.of(1963, 12, 31)
        private val inntekt =
            Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, 2023, REGISTRERT_INNTEKT.toBigDecimal())
        private val personalSpec = PersonalSimuleringSpec(pid, REGISTRERT_INNTEKT, Sivilstand.UOPPGITT)

        private val simuleringsresultat =
            Simuleringsresultat(
                alderspensjon = listOf(SimulertAlderspensjon(alder = 67, beloep = PENSJONSBELOEP)),
                afpPrivat = emptyList()
            )

        private fun impersonalSimuleringSpec(forventetInntekt: Int?, sivilstand: Sivilstand?) =
            ImpersonalSimuleringSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                sivilstand = sivilstand,
                epsHarInntektOver2G = false,
                forventetAarligInntektFoerUttak = forventetInntekt,
                heltUttak = HeltUttak(
                    uttakFomAlder = Alder(67, 1),
                    aarligInntekt = 0,
                    inntektTomAlder = Alder(99, 11),
                    foedselDato = foedselDato
                )
            )
    }
}
