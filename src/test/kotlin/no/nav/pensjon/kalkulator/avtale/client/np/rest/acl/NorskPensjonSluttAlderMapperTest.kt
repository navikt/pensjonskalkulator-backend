package no.nav.pensjon.kalkulator.avtale.client.np.rest.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class NorskPensjonSluttAlderMapperTest : ShouldSpec({

    should("adjust sluttår when the last payment period ends in January") {
        NorskPensjonSluttAlderMapper.sluttAar(
            sluttAlder = 70,
            perioder = listOf(periode(LocalDate.of(2040, 1, 1))),
            foedselsdato = FOEDSELSDATO
        ) shouldBe 69
    }

    should("do not adjust sluttår when there are no payment periods") {
        NorskPensjonSluttAlderMapper.sluttAar(
            sluttAlder = 70,
            perioder = emptyList(),
            foedselsdato = FOEDSELSDATO
        ) shouldBe 70
    }

    should("do not adjust sluttår when a payment period is lifelong") {
        NorskPensjonSluttAlderMapper.sluttAar(
            sluttAlder = 70,
            perioder = listOf(
                periode(LocalDate.of(2040, 1, 1)),
                periode(datoTom = null)
            ),
            foedselsdato = FOEDSELSDATO
        ) shouldBe 70
    }

    should("adjust sluttår when the last payment period is the last day of the month and the birthday is the last day of the month") {
        NorskPensjonSluttAlderMapper.sluttAar(
            sluttAlder = 70,
            perioder = listOf(
                periode(LocalDate.of(2040, 1, 31))
            ),
            foedselsdato = LocalDate.of(1970, 1, 31)
        ) shouldBe 69
    }
})

private val FOEDSELSDATO = LocalDate.of(1970, 1, 1)

private fun periode(datoTom: LocalDate?) =
    UtbetalingsperiodeDto().apply { this.datoTom = datoTom }
