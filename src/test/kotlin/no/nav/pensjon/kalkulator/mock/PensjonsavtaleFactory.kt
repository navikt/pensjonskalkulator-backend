package no.nav.pensjon.kalkulator.mock

import no.nav.pensjon.kalkulator.avtale.*

object PensjonsavtaleFactory {

    fun pensjonsavtaler() = pensjonsavtaler(67)

    fun pensjonsavtaler(startalder: Int) = Pensjonsavtaler(listOf(pensjonsavtale(startalder)), listOf(selskap()))

    // Avtaler med felter støttet i versjon 3 av tjenesten til Norsk Pensjon
    fun pensjonsavtalerV3() = Pensjonsavtaler(listOf(avtaleMedToUtbetalingsperioder()), listOf(selskapV3()))

    fun avtaleMedToUtbetalingsperioder() =
        Pensjonsavtale(
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
            70,
            80,
            "2023-01-01",
            ManglendeEksternGraderingAarsak.IKKE_STOETTET,
            ManglendeEksternBeregningAarsak.UKJENT_PRODUKTTYPE,
            listOf(
                utbetalingsperiodeMedSluttalder(),
                utbetalingsperiodeUtenSluttalder()
            )
        )

    private fun utbetalingsperiodeMedSluttalder() =
        Utbetalingsperiode(
            Alder(71, 1),
            Alder(81, 2),
            10000,
            Uttaksgrad.HUNDRE_PROSENT
        )

    private fun utbetalingsperiodeUtenSluttalder() =
        Utbetalingsperiode(
            Alder(72, 2),
            null,
            20000,
            Uttaksgrad.AATTI_PROSENT
        )


    private fun pensjonsavtale(startalder: Int) =
        Pensjonsavtale(
            "produkt1",
            AvtaleKategori.INDIVIDUELL_ORDNING,
            startalder,
            77,
            listOf(utbetalingsperiode())
        )

    private fun utbetalingsperiode() =
        Utbetalingsperiode(
            Alder(68, 1),
            Alder(78, 12),
            123000,
            Uttaksgrad.HUNDRE_PROSENT
        )

    private fun selskap() = Selskap("selskap1", true)

    // Selskap med felter støttet i versjon 3 av tjenesten til Norsk Pensjon
    private fun selskapV3() = Selskap(
        "Selskap1",
        true,
        1,
        AvtaleKategori.FOLKETRYGD,
        "Feil1"
    )
}
