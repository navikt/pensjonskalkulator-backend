package no.nav.pensjon.kalkulator.sak.api.map

import no.nav.pensjon.kalkulator.sak.api.dto.SakDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SakMapperTest {

    @Test
    fun `toDto maps harSak to harUfoeretrygdEllerGjenlevendeytelse`() {
        assertEquals(SakDto(harUfoeretrygdEllerGjenlevendeytelse = false), SakMapper.toDto(harSak = false))
        assertEquals(SakDto(harUfoeretrygdEllerGjenlevendeytelse = true), SakMapper.toDto(harSak = true))
    }
}
