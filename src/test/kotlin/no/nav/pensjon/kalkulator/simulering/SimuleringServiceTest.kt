package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.person.Land
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Pid
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

        assertEquals(123456, response.pensjon[0].belop)
        verifyNoInteractions(opptjeningsgrunnlagClient, personClient)
    }

    @Test
    fun `simulerAlderspensjon obtains inntekt and sivilstand when not specified`() {
        val spec = simuleringSpec(null, null)
        arrangePidAndResultat()
        `when`(opptjeningsgrunnlagClient.getOpptjeningsgrunnlag(anyObject())).thenReturn(opptjeningsgrunnlag)
        `when`(personClient.getPerson(anyObject())).thenReturn(person)

        val response = service.simulerAlderspensjon(spec)

        assertEquals(123456, response.pensjon[0].belop)
        verify(opptjeningsgrunnlagClient, times(1)).getOpptjeningsgrunnlag(pid)
        verify(personClient, times(1)).getPerson(pid)
    }

    private fun arrangePidAndResultat() {
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(simuleringClient.simulerAlderspensjon(anyObject())).thenReturn(simuleringsresultat)
    }

    private fun <T> anyObject(): T {
        return any()
    }

    private companion object {

        private val pid = Pid("12906498357")
        private val foedselsdato = LocalDate.of(1964, 10, 12)
        private val foersteUttaksdato = LocalDate.of(2023, 1, 1)
        private val person = Person(foedselsdato, Land.NORGE, Sivilstand.UOPPGITT)
        private val inntekt = Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, 2023, BigDecimal("543210"))
        private val opptjeningsgrunnlag = Opptjeningsgrunnlag(listOf(inntekt))
        private val simuleringsresultat = Simuleringsresultat(
            pensjon = listOf(
                SimulertAlderspensjon(
                    alder = 67,
                    belop = 123456,
                )
            )
        )

        private fun simuleringSpec(forventetInntekt: Int?, sivilstand: Sivilstand?) =
            SimuleringSpecDto("ALDER", forventetInntekt, 100, foersteUttaksdato, sivilstand, false)
    }
}
