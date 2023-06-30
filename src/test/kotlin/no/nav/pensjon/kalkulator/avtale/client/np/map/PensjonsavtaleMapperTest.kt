package no.nav.pensjon.kalkulator.avtale.client.np.map

import no.nav.pensjon.kalkulator.avtale.client.np.dto.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PensjonsavtaleMapperTest {

    @Test
    fun `fromDto maps DTO to avtale and selskap`() {
        val result = PensjonsavtaleMapper.fromDto(envelope())

        val avtale = result.avtaler[0]
        assertEquals("produkt1", avtale.produktbetegnelse)
        assertEquals("kategori1", avtale.kategori)
        assertEquals(67, avtale.startAlder)
        assertEquals(77, avtale.sluttAlder)
        val utbetalingsperiode = avtale.utbetalingsperioder[0]
        val start = utbetalingsperiode.start
        val slutt = utbetalingsperiode.slutt!!
        assertEquals(68, start.aar)
        assertEquals(1, start.maaned)
        assertEquals(78, slutt.aar)
        assertEquals(12, slutt.maaned)
        assertEquals(123000, utbetalingsperiode.aarligUtbetaling)
        assertEquals(100, utbetalingsperiode.grad)
        val selskap = result.utilgjengeligeSelskap[0]
        assertEquals("selskap1", selskap.navn)
        assertTrue(selskap.heltUtilgjengelig)
    }

    private companion object {
        private fun envelope() = EnvelopeDto().apply { body = body() }

        private fun body() = BodyDto().apply { privatPensjonsrettigheter = pensjonsrettigheter() }

        private fun pensjonsrettigheter() =
            PrivatPensjonsrettigheterDto().apply {
                privatAlderRettigheter = listOf(alderRettighet())
                utilgjengeligeSelskap = listOf(selskap())
            }

        private fun alderRettighet() =
            PrivatAlderRettigheterDto().apply {
                produktbetegnelse = "produkt1"
                kategori = "kategori1"
                startAlder = 67
                sluttAlder = 77
                utbetalingsperioder = listOf(utbetalingsperiode())
            }

        private fun utbetalingsperiode() =
            UtbetalingsperioderDto().apply {
                startAlder = 68
                startMaaned = 1
                sluttAlder = 78
                sluttMaaned = 12
                aarligUtbetaling = 123000
                grad = 100
            }

        private fun selskap() =
            SelskapDto().apply {
                navn = "selskap1"
                heltUtilgjengelig = true
            }
    }
}
