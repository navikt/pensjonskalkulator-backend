package no.nav.pensjon.kalkulator.avtale.client.np.map

import no.nav.pensjon.kalkulator.avtale.client.np.dto.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PensjonsavtaleMapperTest {

    @Test
    fun `fromDto maps DTO to avtale`() {
        val avtale = PensjonsavtaleMapper.fromDto(envelope())

        assertEquals("produkt1", avtale.produktbetegnelse)
        assertEquals("kategori1", avtale.kategori)
        assertEquals(67, avtale.startAlder)
        assertEquals(77, avtale.sluttAlder)
        val utbetalingsperiode = avtale.utbetalingsperiode
        val start = utbetalingsperiode.start
        val slutt = utbetalingsperiode.slutt!!
        assertEquals(68, start.aar)
        assertEquals(1, start.maaned)
        assertEquals(78, slutt.aar)
        assertEquals(12, slutt.maaned)
        assertEquals(123000, utbetalingsperiode.aarligUtbetaling)
        assertEquals(100, utbetalingsperiode.grad)
    }

    private companion object {
        private fun envelope() = EnvelopeDto().apply { body = body() }

        private fun body() = BodyDto().apply { privatPensjonsrettigheter = pensjonsrettigheter() }

        private fun pensjonsrettigheter() =
            PrivatPensjonsrettigheterDto().apply { privatAlderRettigheterDto = alderRettigheter() }

        private fun alderRettigheter() =
            PrivatAlderRettigheterDto().apply {
                produktbetegnelse = "produkt1"
                kategori = "kategori1"
                startAlder = 67
                sluttAlder = 77
                utbetalingsperioder = utbetalingsperioder()
            }

        private fun utbetalingsperioder() =
            UtbetalingsperioderDto().apply {
                startAlder = 68
                startMaaned = 1
                sluttAlder = 78
                sluttMaaned = 12
                aarligUtbetaling = 123000
                grad = 100
            }
    }
}
