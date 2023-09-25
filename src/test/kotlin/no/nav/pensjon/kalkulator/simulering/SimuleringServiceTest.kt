package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringAlderDto
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringSpecDto
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
    private lateinit var opptjeningsgrunnlagClient: OpptjeningsgrunnlagClient

    @Mock
    private lateinit var personClient: PersonClient

    @Mock
    private lateinit var pidGetter: PidGetter

    @BeforeEach
    fun initialize() {
        service = SimuleringService(simuleringClient, opptjeningsgrunnlagClient, personClient, pidGetter)
        `when`(pidGetter.pid()).thenReturn(pid)
    }

    @Test
    fun `simulerAlderspensjon uses specified inntekt and sivilstand`() {
        val incomingSpec = incomingSimuleringSpec(FORVENTET_INNTEKT, Sivilstand.UGIFT)
        `when`(
            simuleringClient.simulerAlderspensjon(
                internalSimuleringSpec(
                    FORVENTET_INNTEKT,
                    Sivilstand.UGIFT
                )
            )
        ).thenReturn(simuleringsresultat)

        val response = service.simulerAlderspensjon(incomingSpec)

        assertEquals(123456, response.alderspensjon[0].beloep)
        verifyNoInteractions(opptjeningsgrunnlagClient, personClient)
    }

    @Test
    fun `simulerAlderspensjon obtains registrert inntekt and sivilstand when not specified`() {
        val incomingSpec = incomingSimuleringSpec(null, null)
        `when`(opptjeningsgrunnlagClient.fetchOpptjeningsgrunnlag(pid)).thenReturn(opptjeningsgrunnlag)
        `when`(personClient.fetchPerson(pid)).thenReturn(person())
        `when`(
            simuleringClient.simulerAlderspensjon(
                internalSimuleringSpec(
                    REGISTRERT_INNTEKT,
                    Sivilstand.UOPPGITT
                )
            )
        ).thenReturn(simuleringsresultat)

        val response = service.simulerAlderspensjon(incomingSpec)

        assertEquals(PENSJONSBELOEP, response.alderspensjon[0].beloep)
        verify(opptjeningsgrunnlagClient, times(1)).fetchOpptjeningsgrunnlag(pid)
        verify(personClient, times(1)).fetchPerson(pid)
    }

    private companion object {
        private const val FORVENTET_INNTEKT = 654321
        private const val REGISTRERT_INNTEKT = 543210
        private const val PENSJONSBELOEP = 123456
        private val foersteUttaksdato = LocalDate.of(2031, 2, 1)
        private val foedselsdato = LocalDate.of(1963, 12, 31)
        private val inntekt =
            Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, 2023, REGISTRERT_INNTEKT.toBigDecimal())
        private val opptjeningsgrunnlag = Opptjeningsgrunnlag(listOf(inntekt))

        private fun incomingSimuleringSpec(forventetInntekt: Int?, sivilstand: Sivilstand?) =
            SimuleringSpecDto(
                SimuleringType.ALDERSPENSJON,
                forventetInntekt,
                100,
                SimuleringAlderDto(67, 1),
                foedselsdato,
                sivilstand,
                false
            )

        private fun internalSimuleringSpec(forventetInntekt: Int, sivilstand: Sivilstand) =
            SimuleringSpec(
                SimuleringType.ALDERSPENSJON,
                pid,
                forventetInntekt,
                100,
                foersteUttaksdato,
                sivilstand,
                false
            )

        private val simuleringsresultat =
            Simuleringsresultat(
                alderspensjon = listOf(
                    SimulertAlderspensjon(
                        alder = 67,
                        beloep = PENSJONSBELOEP,
                    )
                ),
                afpPrivat = emptyList()
            )
    }
}
