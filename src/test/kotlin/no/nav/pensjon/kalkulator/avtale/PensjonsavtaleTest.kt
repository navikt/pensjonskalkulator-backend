package no.nav.pensjon.kalkulator.avtale

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class PensjonsavtaleTest : ShouldSpec({

    context("erLivsvarig") {
        should("be true when sluttalder not defined, false otherwise") {
            pensjonsavtale1(startalder = 67, sluttalder = null).erLivsvarig shouldBe true
            pensjonsavtale2(startalder = 67, sluttalder = null).erLivsvarig shouldBe true
            pensjonsavtale1(startalder = 67, sluttalder = 99).erLivsvarig shouldBe false
            pensjonsavtale2(startalder = 67, sluttalder = 0).erLivsvarig shouldBe false
        }
    }

    context("harStartalder") {
        should("be true when startalder greater than zero, false otherwise") {
            pensjonsavtale1(startalder = 67, sluttalder = 99).harStartAar shouldBe true
            pensjonsavtale2(startalder = 1, sluttalder = 99).harStartAar shouldBe true
            pensjonsavtale1(startalder = 0, sluttalder = 99).harStartAar shouldBe false
            pensjonsavtale2(startalder = -67, sluttalder = 99).harStartAar shouldBe false
        }
    }
})

private fun pensjonsavtale1(startalder: Int, sluttalder: Int?) =
    Pensjonsavtale(
        avtalenummer = "Avtale",
        arbeidsgiver = "Firma1",
        selskapsnavn = "Selskap1",
        produktbetegnelse = "Produkt1",
        kategori = AvtaleKategori.INDIVIDUELL_ORDNING,
        underkategori = AvtaleUnderkategori.FORENINGSKOLLEKTIV,
        innskuddssaldo = 1000,
        naavaerendeAvtaltAarligInnskudd = 100,
        pensjonsbeholdningForventet = 1000000,
        pensjonsbeholdningNedreGrense = 900000,
        pensjonsbeholdningOvreGrense = 1100000,
        avkastningsgaranti = false,
        beregningsmodell = EksternBeregningsmodell.BRANSJEAVTALE,
        startAar = startalder,
        sluttAar = sluttalder,
        opplysningsdato = "2023-01-01",
        manglendeGraderingAarsak = ManglendeEksternGraderingAarsak.IKKE_STOETTET,
        manglendeBeregningAarsak = ManglendeEksternBeregningAarsak.UKJENT_PRODUKTTYPE,
        utbetalingsperioder = emptyList()
    )

private fun pensjonsavtale2(startalder: Int, sluttalder: Int?) =
    Pensjonsavtale(
        produktbetegnelse = "",
        kategori = AvtaleKategori.NONE,
        startalder = startalder,
        sluttalder = sluttalder,
        utbetalingsperioder = emptyList()
    )
