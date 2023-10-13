package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.UtbetalingsperiodeDto
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class NorskPensjonSluttAlderMapperTest {

    @Test
    fun `sluttAar is null when avtalens sluttAlder is null`() {
        assertNull(NorskPensjonSluttAlderMapper.sluttAar(null, null))
        assertNull(NorskPensjonSluttAlderMapper.sluttAar(null, listOf(sluttMidtenAvAaret())))
    }

    @Test
    fun `sluttAar not adjusted when siste utbetalingsperiode slutter etter maaned 1`() {
        assertEquals(70, NorskPensjonSluttAlderMapper.sluttAar(70, null))
        assertEquals(70, NorskPensjonSluttAlderMapper.sluttAar(70, emptyList()))
        assertEquals(70, NorskPensjonSluttAlderMapper.sluttAar(70, listOf(evig())))
        assertEquals(70, NorskPensjonSluttAlderMapper.sluttAar(70, listOf(sluttMidtenAvAaret())))
        assertEquals(70, NorskPensjonSluttAlderMapper.sluttAar(70, listOf(sluttStartenAvAaret(), evig())))
        assertEquals(70, NorskPensjonSluttAlderMapper.sluttAar(70, listOf(slutt(70, 2))))
        assertEquals(70, NorskPensjonSluttAlderMapper.sluttAar(70, listOf(slutt(63, 12), slutt(70, 6), slutt(66, 1))))
        assertEquals(70, NorskPensjonSluttAlderMapper.sluttAar(70, listOf(slutt(70, 12), slutt(70, 1))))
    }

    @Test
    fun `sluttAar adjusted when siste utbetalingsperiode slutter i maaned 1`() {
        assertEquals(69, NorskPensjonSluttAlderMapper.sluttAar(70, listOf(sluttStartenAvAaret())))
        assertEquals(69, NorskPensjonSluttAlderMapper.sluttAar(70, listOf(slutt(63, 12), slutt(69, 1), slutt(66, 6))))
        assertEquals(69, NorskPensjonSluttAlderMapper.sluttAar(70, listOf(slutt(70, 1), slutt(70, 1))))
    }

    @Test
    fun `default maaned is 1`() {
        assertEquals(69, NorskPensjonSluttAlderMapper.sluttAar(70, listOf(slutt(70, null))))
    }

    private fun evig() = slutt(null, null)

    private fun sluttMidtenAvAaret() = slutt(70, 7)

    private fun sluttStartenAvAaret() = slutt(70, 1)

    private fun slutt(alder: Int?, maaned: Int?) =
        UtbetalingsperiodeDto().apply {
            sluttAlder = alder
            sluttMaaned = maaned
        }
}
