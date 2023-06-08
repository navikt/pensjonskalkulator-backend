package no.nav.pensjon.kalkulator.avtale.api.map

import no.nav.pensjon.kalkulator.avtale.Alder
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.Utbetalingsperiode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PensjonsavtaleMapperTest {

    @Test
    fun `toDto maps avtaler to DTO`() {
        val dto = PensjonsavtaleMapper.toDto(pensjonsavtaler())

        val avtale = dto.avtaler[0]
        assertEquals("produkt1", avtale.produktbetegnelse)
        assertEquals("kategori1", avtale.kategori)
        assertEquals(67, avtale.startAlder)
        assertEquals(77, avtale.sluttAlder)
        val utbetalingsperiode = avtale.utbetalingsperiode
        assertEquals(68, utbetalingsperiode.startAlder)
        assertEquals(1, utbetalingsperiode.startMaaned)
        assertEquals(78, utbetalingsperiode.sluttAlder)
        assertEquals(12, utbetalingsperiode.sluttMaaned)
        assertEquals(123000, utbetalingsperiode.aarligUtbetaling)
        assertEquals(100, utbetalingsperiode.grad)
    }


    private companion object {

        private fun pensjonsavtaler() = Pensjonsavtaler(listOf(pensjonsavtale()))

        private fun pensjonsavtale() = Pensjonsavtale(
            "produkt1",
            "kategori1",
            67,
            77,
            utbetalingsperioder()
        )

        private fun utbetalingsperioder() = Utbetalingsperiode(
            Alder(68, 1),
            Alder(78, 12),
            123000,
            100
        )
    }
}
