package no.nav.pensjon.kalkulator.avtale.api.map

import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.pensjonsavtaler
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
        val utbetalingsperiode = avtale.utbetalingsperioder[0]
        assertEquals(68, utbetalingsperiode.startAlder)
        assertEquals(1, utbetalingsperiode.startMaaned)
        assertEquals(78, utbetalingsperiode.sluttAlder)
        assertEquals(12, utbetalingsperiode.sluttMaaned)
        assertEquals(123000, utbetalingsperiode.aarligUtbetaling)
        assertEquals(100, utbetalingsperiode.grad)
        val selskap = dto.utilgjengeligeSelskap[0]
        assertEquals("selskap1", selskap.navn)
        assertTrue(selskap.heltUtilgjengelig)
    }
}
