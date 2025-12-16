package no.nav.pensjon.kalkulator.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import java.math.BigDecimal
import java.time.LocalDateTime

class FremtidigInntektV2ServiceTest : ShouldSpec({

    should("returnere beløp for 'sum pensjonsgivende inntekt'") {
        val result = InntektService(
            client = arrangeOpptjening(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT),
            pidGetter
        ) { now }.sistePensjonsgivendeInntekt()

        with(result) {
            beloep shouldBe BigDecimal("123000")
            aar shouldBe 2022
        }
    }

    should("returnere 0 når ingen 'sum pensjonsgivende inntekt' finnes") {
        val result = InntektService(
            client = arrangeOpptjening(Opptjeningstype.PENSJONSGIVENDE_INNTEKT),
            pidGetter
        ) { now }.sistePensjonsgivendeInntekt()

        with(result) {
            beloep shouldBe BigDecimal.ZERO
            aar shouldBe 2022
        }
    }
})

private const val CURRENT_AAR = 2024
private val now = LocalDateTime.of(CURRENT_AAR, 1, 1, 12, 0, 0)
private val pidGetter = mockk<PidGetter>(relaxed = true)

private fun arrangeOpptjening(opptjeningstype: Opptjeningstype): OpptjeningsgrunnlagClient =
    mockk<OpptjeningsgrunnlagClient>().apply {
        every {
            fetchOpptjeningsgrunnlag(any())
        } returns Opptjeningsgrunnlag(inntekter = listOf(inntekt(opptjeningstype)))
    }

private fun inntekt(type: Opptjeningstype) =
    Inntekt(
        type,
        aar = CURRENT_AAR - 2,
        beloep = BigDecimal("123000")
    )
