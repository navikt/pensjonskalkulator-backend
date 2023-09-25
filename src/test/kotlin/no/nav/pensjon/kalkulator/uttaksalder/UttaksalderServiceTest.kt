package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderIngressSpecDto
import no.nav.pensjon.kalkulator.uttaksalder.client.UttaksalderClient
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal

@ExtendWith(SpringExtension::class)
internal class UttaksalderServiceTest {

    private lateinit var service: UttaksalderService

    @Mock
    private lateinit var uttaksalderClient: UttaksalderClient

    @Mock
    private lateinit var opptjeningsgrunnlagClient: OpptjeningsgrunnlagClient

    @Mock
    private lateinit var personClient: PersonClient

    @Mock
    private lateinit var pidGetter: PidGetter

    @BeforeEach
    fun initialize() {
        service = UttaksalderService(uttaksalderClient, opptjeningsgrunnlagClient, personClient, pidGetter)

        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(uttaksalderClient.finnTidligsteUttaksalder(anyObject())).thenReturn(uttaksalder)
    }

    @Test
    fun `finnTidligsteUttaksalder uses properties from spec`() {
        val spec = UttaksalderIngressSpecDto(Sivilstand.GIFT, true, 100_000)
        val uttaksalder = service.finnTidligsteUttaksalder(spec)

        assertNotNull(uttaksalder)
        verify(uttaksalderClient, times(1)).finnTidligsteUttaksalder(
            UttaksalderSpec(pid, Sivilstand.GIFT, true, 100_000)
        )
        verify(personClient, never()).fetchPerson(anyObject())
        verify(opptjeningsgrunnlagClient, never()).fetchOpptjeningsgrunnlag(anyObject())
    }

    @Test
    fun `finnTidligsteUttaksalder obtains inntekt and sivilstand when not specified`() {
        val person = person()
        `when`(opptjeningsgrunnlagClient.fetchOpptjeningsgrunnlag(anyObject())).thenReturn(opptjeningsgrunnlag)
        `when`(personClient.fetchPerson(pid)).thenReturn(person)

        val spec = UttaksalderIngressSpecDto(null, null, null)
        val uttaksalder = service.finnTidligsteUttaksalder(spec)

        assertNotNull(uttaksalder)
        verify(uttaksalderClient, times(1)).finnTidligsteUttaksalder(
            UttaksalderSpec(pid, person.sivilstand, false, inntekt.beloep.toInt())
        )
        verify(personClient, times(1)).fetchPerson(pid)
        verify(opptjeningsgrunnlagClient, times(1)).fetchOpptjeningsgrunnlag(pid)
    }

    private fun <T> anyObject(): T {
        return any()
    }

    private companion object {
        private val uttaksalder = Alder(67, 0)
        private val inntekt = Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, 2023, BigDecimal("543210"))
        private val opptjeningsgrunnlag = Opptjeningsgrunnlag(listOf(inntekt))
    }
}
