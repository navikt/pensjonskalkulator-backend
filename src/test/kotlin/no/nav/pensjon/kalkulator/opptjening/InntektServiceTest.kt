package no.nav.pensjon.kalkulator.opptjening

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
class InntektServiceTest {

    private lateinit var service: InntektService

    @Mock
    private lateinit var client: OpptjeningsgrunnlagClient

    @Mock
    private lateinit var pidGetter: PidGetter

    @BeforeEach
    fun initialize() {
        service = InntektService(client, pidGetter) { now }
    }

    @Test
    fun `sistePensjonsgivendeInntekt returns beloep for 'sum pensjonsgivende inntekt'`() {
        arrangePidAndResultat(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT)

        val result = service.sistePensjonsgivendeInntekt()

        assertEquals(BigDecimal("123000"), result.beloep)
        assertEquals(2022, result.aar)
    }

    @Test
    fun `sistePensjonsgivendeInntekt returns zero when no 'sum pensjonsgivende inntekt' entries`() {
        arrangePidAndResultat(Opptjeningstype.PENSJONSGIVENDE_INNTEKT)

        val result = service.sistePensjonsgivendeInntekt()

        assertEquals(BigDecimal.ZERO, result.beloep)
        assertEquals(2022, result.aar)
    }

    private fun arrangePidAndResultat(opptjeningstype: Opptjeningstype) {
        `when`(pidGetter.pid()).thenReturn(pid)

        `when`(client.fetchOpptjeningsgrunnlag(pid)).thenReturn(
            Opptjeningsgrunnlag(listOf(inntekt(opptjeningstype)))
        )
    }

    private companion object {

        private const val CURRENT_AAR = 2024
        private val now = LocalDateTime.of(CURRENT_AAR, 1, 1, 12, 0, 0)

        private fun inntekt(opptjeningstype: Opptjeningstype) =
            Inntekt(
                type = opptjeningstype,
                aar = CURRENT_AAR - 2,
                beloep = BigDecimal("123000")
            )
    }
}
