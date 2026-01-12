package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.UtbetalingsperiodeDto

class NorskPensjonSluttAlderMapperTest : ShouldSpec({

    context("sluttAar") {
        should("be null when avtalens sluttalder is null") {
            NorskPensjonSluttAlderMapper.sluttAar(sluttAlder = null, perioder = null) shouldBe null

            NorskPensjonSluttAlderMapper.sluttAar(
                sluttAlder = null,
                perioder = listOf(sluttMidtenAvAaret())
            ) shouldBe null
        }

        should("not be adjusted when siste utbetalingsperiode slutter etter måned 1") {
            NorskPensjonSluttAlderMapper.sluttAar(sluttAlder = 70, perioder = null) shouldBe 70
            NorskPensjonSluttAlderMapper.sluttAar(sluttAlder = 70, perioder = emptyList()) shouldBe 70
            NorskPensjonSluttAlderMapper.sluttAar(sluttAlder = 70, perioder = listOf(evig())) shouldBe 70
            NorskPensjonSluttAlderMapper.sluttAar(sluttAlder = 70, perioder = listOf(sluttMidtenAvAaret())) shouldBe 70

            NorskPensjonSluttAlderMapper.sluttAar(
                sluttAlder = 70,
                perioder = listOf(sluttStartenAvAaret(), evig())
            ) shouldBe 70

            NorskPensjonSluttAlderMapper.sluttAar(
                sluttAlder = 70,
                perioder = listOf(slutt(alder = 70, maaned = 2))
            ) shouldBe 70

            NorskPensjonSluttAlderMapper.sluttAar(
                sluttAlder = 70,
                perioder = listOf(
                    slutt(alder = 63, maaned = 12),
                    slutt(alder = 70, maaned = 6),
                    slutt(alder = 66, maaned = 1)
                )
            ) shouldBe 70

            NorskPensjonSluttAlderMapper.sluttAar(
                sluttAlder = 70,
                perioder = listOf(slutt(alder = 70, maaned = 12), slutt(alder = 70, maaned = 1))
            ) shouldBe 70
        }

        should("be adjusted when siste utbetalingsperiode slutter i måned 1") {
            NorskPensjonSluttAlderMapper.sluttAar(sluttAlder = 70, perioder = listOf(sluttStartenAvAaret())) shouldBe 69

            NorskPensjonSluttAlderMapper.sluttAar(
                sluttAlder = 70,
                perioder = listOf(
                    slutt(alder = 63, maaned = 12),
                    slutt(alder = 69, maaned = 1),
                    slutt(alder = 66, maaned = 6)
                )
            ) shouldBe 69

            NorskPensjonSluttAlderMapper.sluttAar(
                sluttAlder = 70,
                perioder = listOf(slutt(alder = 70, maaned = 1), slutt(alder = 70, maaned = 1))
            ) shouldBe 69
        }

        should("use 1 as default måned") {
            NorskPensjonSluttAlderMapper.sluttAar(
                sluttAlder = 70,
                perioder = listOf(slutt(alder = 70, maaned = null))
            ) shouldBe 69
        }
    }
})

private fun evig() =
    slutt(alder = null, maaned = null)

private fun sluttMidtenAvAaret() =
    slutt(alder = 70, maaned = 7)

private fun sluttStartenAvAaret() =
    slutt(alder = 70, maaned = 1)

private fun slutt(alder: Int?, maaned: Int?) =
    UtbetalingsperiodeDto().apply {
        sluttAlder = alder
        sluttMaaned = maaned
    }

