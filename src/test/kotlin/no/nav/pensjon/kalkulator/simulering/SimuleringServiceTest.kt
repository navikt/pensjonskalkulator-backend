package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
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
import java.math.BigDecimal
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
    }

    @Test
    fun `simulerAlderspensjon uses specified inntekt and sivilstand`() {
        val spec = simuleringSpec(654321, Sivilstand.UOPPGITT)
        arrangePidAndResultat()

        val response = service.simulerAlderspensjon(spec)

        assertEquals(123456, response.alderspensjon[0].beloep)
        verifyNoInteractions(opptjeningsgrunnlagClient, personClient)
    }

    @Test
    fun `simulerAlderspensjon obtains inntekt and sivilstand when not specified`() {
        val spec = simuleringSpec(null, null)
        arrangePidAndResultat()
        `when`(opptjeningsgrunnlagClient.getOpptjeningsgrunnlag(anyObject())).thenReturn(opptjeningsgrunnlag)
        `when`(personClient.fetchPerson(anyObject())).thenReturn(person())

        val response = service.simulerAlderspensjon(spec)

        assertEquals(123456, response.alderspensjon[0].beloep)
        verify(opptjeningsgrunnlagClient, times(1)).getOpptjeningsgrunnlag(pid)
        verify(personClient, times(1)).fetchPerson(pid)
    }

    private fun arrangePidAndResultat() {
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(simuleringClient.simulerAlderspensjon(anyObject())).thenReturn(simuleringsresultat)
    }

    private fun <T> anyObject(): T {
        return any()
    }

    private companion object {
        private val foersteUttaksdato = LocalDate.of(2023, 1, 1)
        private val inntekt = Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, 2023, BigDecimal("543210"))
        private val opptjeningsgrunnlag = Opptjeningsgrunnlag(listOf(inntekt))

        private val simuleringsresultat = Simuleringsresultat(
            alderspensjon = listOf(
                SimulertAlderspensjon(
                    alder = 67,
                    beloep = 123456,
                )
            ),
            afpPrivat = emptyList(),
        )

        private fun simuleringSpec(forventetInntekt: Int?, sivilstand: Sivilstand?) =
            SimuleringSpecDto(SimuleringType.ALDERSPENSJON, forventetInntekt, 100, foersteUttaksdato, sivilstand, false)
    }
}
