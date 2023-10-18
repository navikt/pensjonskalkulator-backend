package no.nav.pensjon.kalkulator.opptjening.api.map

import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class InntektMapperTest {

    @Test
    fun `toDto maps aar and beloep`() {
        val dto = InntektMapper.toDto(Inntekt(Opptjeningstype.OTHER, 2023, BigDecimal.TEN))

        assertEquals(2023, dto.aar)
        assertEquals(10, dto.beloep)
    }

    @Test
    fun `toDto maps decimal beloep to integer, rounding down`() {
        val dto = InntektMapper.toDto(Inntekt(Opptjeningstype.OTHER, 2023, BigDecimal("67.89")))
        assertEquals(67, dto.beloep)
    }
}
