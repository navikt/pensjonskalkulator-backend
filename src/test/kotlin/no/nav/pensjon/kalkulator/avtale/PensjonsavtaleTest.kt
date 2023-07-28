package no.nav.pensjon.kalkulator.avtale

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class PensjonsavtaleTest {

    @Test
    fun `erLivsvarig is true when sluttalder not defined, false otherwise`() {
        assertTrue(pensjonsavtale1(67, null).erLivsvarig)
        assertTrue(pensjonsavtale2(67, null).erLivsvarig)
        assertFalse(pensjonsavtale1(67, 99).erLivsvarig)
        assertFalse(pensjonsavtale2(67, 0).erLivsvarig)
    }

    @Test
    fun `harStartalder is true when startalder greater than zero, false otherwise`() {
        assertTrue(pensjonsavtale1(67, 99).harStartalder)
        assertTrue(pensjonsavtale2(1, 99).harStartalder)
        assertFalse(pensjonsavtale1(0, 99).harStartalder)
        assertFalse(pensjonsavtale2(-67, 99).harStartalder)
    }

    private fun pensjonsavtale1(startalder: Int, sluttalder: Int?) = Pensjonsavtale(
        1,
        "Firma1",
        "Selskap1",
        "Produkt1",
        AvtaleKategori.INDIVIDUELL_ORDNING,
        AvtaleUnderkategori.FORENINGSKOLLEKTIV,
        1000,
        100,
        1000000,
        900000,
        1100000,
        false,
        EksternBeregningsmodell.BRANSJEAVTALE,
        startalder,
        sluttalder,
        "2023-01-01",
        ManglendeEksternGraderingAarsak.IKKE_STOETTET,
        ManglendeEksternBeregningAarsak.UKJENT_PRODUKTTYPE,
        emptyList()
    )

    private fun pensjonsavtale2(startalder: Int, sluttalder: Int?) =
        Pensjonsavtale("", AvtaleKategori.NONE, startalder, sluttalder, emptyList())
}
