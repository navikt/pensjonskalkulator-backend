package no.nav.pensjon.kalkulator.avtale.client.np.compare

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.AvtaleKategori
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.Utbetalingsperiode
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad

class PensjonsavtaleComparatorTest : ShouldSpec({

    should("ikke finne diff for identiske avtaler") {
        val avtaler = pensjonsavtaler(avtale(periode()))

        PensjonsavtaleComparator.finnDiff(avtaler, avtaler) shouldBe false
    }

    should("ikke finne diff når REST-sluttalder er én måned senere") {
        val soap = pensjonsavtaler(avtale(periode(sluttAlder = Alder(67, 11))))
        val rest = pensjonsavtaler(avtale(periode(sluttAlder = Alder(68, 0))))

        PensjonsavtaleComparator.finnDiff(soap, rest) shouldBe false
    }

    should("finne diff når sluttalder avviker med to måneder") {
        val soap = pensjonsavtaler(avtale(periode(sluttAlder = Alder(67, 10))))
        val rest = pensjonsavtaler(avtale(periode(sluttAlder = Alder(68, 0))))

        PensjonsavtaleComparator.finnDiff(soap, rest) shouldBe true
    }

    should("finne diff ved ulikt periodebeløp") {
        val soap = pensjonsavtaler(avtale(periode(beloep = 10_000)))
        val rest = pensjonsavtaler(avtale(periode(beloep = 20_000)))

        PensjonsavtaleComparator.finnDiff(soap, rest) shouldBe true
    }

    should("finne diff ved ulik startalder") {
        val soap = pensjonsavtaler(avtale(periode(startAlder = Alder(67, 0))))
        val rest = pensjonsavtaler(avtale(periode(startAlder = Alder(67, 1))))

        PensjonsavtaleComparator.finnDiff(soap, rest) shouldBe true
    }

    should("finne diff ved ulikt antall perioder") {
        val soap = pensjonsavtaler(avtale(periode()))
        val rest = pensjonsavtaler(avtale(periode(), periode(beloep = 20_000)))

        PensjonsavtaleComparator.finnDiff(soap, rest) shouldBe true
    }

    should("finne diff når første periode er forskjellig og neste periode er lik") {
        val likPeriode = periode(beloep = 20_000, startAlder = Alder(68, 0))
        val soap = pensjonsavtaler(avtale(periode(beloep = 10_000), likPeriode))
        val rest = pensjonsavtaler(avtale(periode(beloep = 30_000), likPeriode))

        PensjonsavtaleComparator.finnDiff(soap, rest) shouldBe true
    }

    should("finne diff når siste av flere avtaler er forskjellig") {
        val soap = pensjonsavtaler(
            avtale(periode(beloep = 10_000), startAar = 67),
            avtale(periode(beloep = 20_000), startAar = 68)
        )
        val rest = pensjonsavtaler(
            avtale(periode(beloep = 10_000), startAar = 67),
            avtale(periode(beloep = 30_000), startAar = 68)
        )

        PensjonsavtaleComparator.finnDiff(soap, rest) shouldBe true
    }

    should("finne diff når første av flere avtaler er forskjellig") {
        val soap = pensjonsavtaler(
            avtale(periode(beloep = 10_000), startAar = 67),
            avtale(periode(beloep = 20_000), startAar = 68)
        )
        val rest = pensjonsavtaler(
            avtale(periode(beloep = 30_000), startAar = 67),
            avtale(periode(beloep = 20_000), startAar = 68)
        )

        PensjonsavtaleComparator.finnDiff(soap, rest) shouldBe true
    }

    should("finne diff ved ulikt antall avtaler") {
        val soap = pensjonsavtaler(avtale(periode()))
        val rest = pensjonsavtaler()

        PensjonsavtaleComparator.finnDiff(soap, rest) shouldBe true
    }

    should("slå sammen to SOAP-avtaler når REST har summert beløpene") {
        val soap = pensjonsavtaler(
            avtale(periode(beloep = 10_000)),
            avtale(periode(beloep = 20_000))
        )
        val rest = pensjonsavtaler(avtale(periode(beloep = 30_000)))

        PensjonsavtaleComparator.finnDiff(soap, rest) shouldBe false
    }
})

private fun pensjonsavtaler(vararg avtaler: Pensjonsavtale) =
    Pensjonsavtaler(avtaler = avtaler.toList(), utilgjengeligeSelskap = emptyList())

private fun avtale(
    vararg perioder: Utbetalingsperiode,
    startAar: Int = 67,
    sluttAar: Int? = 77
) = Pensjonsavtale(
    produktbetegnelse = "produkt",
    kategori = AvtaleKategori.NONE,
    startalder = startAar,
    sluttalder = sluttAar,
    utbetalingsperioder = perioder.toList()
)

private fun periode(
    beloep: Int = 10_000,
    startAlder: Alder = Alder(67, 0),
    sluttAlder: Alder? = Alder(77, 0)
) = Utbetalingsperiode(
    startAlder = startAlder,
    sluttAlder = sluttAlder,
    aarligUtbetalingForventet = beloep,
    grad = Uttaksgrad.HUNDRE_PROSENT
)


