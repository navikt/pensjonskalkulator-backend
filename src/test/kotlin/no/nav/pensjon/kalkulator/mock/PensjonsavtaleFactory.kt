package no.nav.pensjon.kalkulator.mock

import no.nav.pensjon.kalkulator.avtale.*

object PensjonsavtaleFactory {

    fun pensjonsavtaler() = Pensjonsavtaler(listOf(pensjonsavtale()), listOf(selskap()))

    private fun pensjonsavtale() =
        Pensjonsavtale(
            "produkt1",
            "kategori1",
            67,
            77,
            utbetalingsperioder()
        )

    private fun utbetalingsperioder() =
        Utbetalingsperiode(
            Alder(68, 1),
            Alder(78, 12),
            123000,
            100
        )

    private fun selskap() = Selskap("selskap1", true)
}
