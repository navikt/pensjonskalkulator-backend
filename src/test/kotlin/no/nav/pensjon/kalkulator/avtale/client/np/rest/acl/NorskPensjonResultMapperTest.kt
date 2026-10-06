package no.nav.pensjon.kalkulator.avtale.client.np.rest.acl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import java.time.LocalDate

class NorskPensjonResultMapperTest : ShouldSpec({

    should("map utbetalingsperiodens LocalDate-felter til Alder") {
        withUtbetalingsperiode(
            datoFom = LocalDate.of(2037, 2, 1),
            datoTom = LocalDate.of(2047, 2, 1),
            aarligUtbetalingForventet = 80_000,
            grad = 100
        ).let {
            it.startAlder shouldBe Alder(67, 0)
            it.sluttAlder shouldBe Alder(77, 0)
            it.aarligUtbetalingForventet shouldBe 80_000
            it.grad shouldBe Uttaksgrad.HUNDRE_PROSENT
        }
    }

    should("not count the birthday as a completed year") {
        withUtbetalingsperiode(
            datoFom = LocalDate.of(2037, 2, 1),
            datoTom = LocalDate.of(2047, 1, 15),
            sluttAlder = 77,
            foedselsdato = LocalDate.of(1970, 1, 15),
            grad = 100
        ).sluttAlder shouldBe Alder(76, 11)
    }

    should("map missing datoTom to a period without end age") {
        withUtbetalingsperiode(datoFom = LocalDate.of(2037, 2, 1)).let {
            it.sluttAlder shouldBe null
            it.aarligUtbetalingForventet shouldBe 0
            it.grad shouldBe Uttaksgrad.NULL
        }
    }

    should("reject a period where datoTom precedes datoFom") {
        shouldThrow<IllegalArgumentException> {
            withUtbetalingsperiode(
                datoFom = LocalDate.of(2037, 2, 1),
                datoTom = LocalDate.of(2036, 2, 1)
            )
        }.message shouldBe "startAlder <= sluttAlder"
    }

    should("fail when required datoFom is missing") {
        shouldThrow<IllegalArgumentException> {
            withUtbetalingsperiode(datoFom = null)
        }.message shouldBe "UtbetalingsperiodeDto mangler datoFom"
    }
})

private fun withUtbetalingsperiode(
    datoFom: LocalDate?,
    datoTom: LocalDate? = null,
    sluttAlder: Int? = null,
    aarligUtbetalingForventet: Int? = null,
    grad: Int = 0,
    foedselsdato: LocalDate = LocalDate.of(1970, 1, 1)
) = NorskPensjonResult().apply {
    pensjonsRettigheter = listOf(
        NorskPensjonPensjonsrettighet().apply {
            this.sluttAlder = sluttAlder
            utbetalingsperioder = listOf(
                UtbetalingsperiodeDto().apply {
                    this.datoFom = datoFom
                    this.datoTom = datoTom
                    this.aarligUtbetalingForventet = aarligUtbetalingForventet
                    this.grad = grad
                }
            )
        }
    )
}.let {
    NorskPensjonResultMapper.fromDto(
        it,
        foedselsdato
    ).avtaler.single().utbetalingsperioder.single()
}
