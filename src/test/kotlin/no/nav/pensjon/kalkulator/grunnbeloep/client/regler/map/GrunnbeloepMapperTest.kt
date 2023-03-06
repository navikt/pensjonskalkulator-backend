package no.nav.pensjon.kalkulator.grunnbeloep.client.regler.map

import no.nav.pensjon.kalkulator.grunnbeloep.client.GrunnbeloepSpec
import no.nav.pensjon.kalkulator.grunnbeloep.client.regler.dto.GrunnbeloepResponseDto
import no.nav.pensjon.kalkulator.grunnbeloep.client.regler.dto.TidsbegrensetVerdiDto
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class GrunnbeloepMapperTest {

    @Test
    fun `toDto maps request spec to DTO`() {
        val spec = GrunnbeloepSpec(
            LocalDate.of(2022, 12, 31),
            LocalDate.of(2023, 1, 1)
        )

        val dto = GrunnbeloepMapper.toDto(spec)

        val fom = asCalendar(dto.fom)
        assertEquals(2022, fom.get(Calendar.YEAR))
        assertEquals(Calendar.DECEMBER, fom.get(Calendar.MONTH))
        assertEquals(31, fom.get(Calendar.DAY_OF_MONTH))
        val tom = asCalendar(dto.tom)
        assertEquals(2023, tom.get(Calendar.YEAR))
        assertEquals(Calendar.JANUARY, tom.get(Calendar.MONTH))
        assertEquals(1, tom.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `fromDto maps response DTO to domain object`() {
        val dto = GrunnbeloepResponseDto(listOf(tidsbegrensetVerdi()))
        val grunnbeloep = GrunnbeloepMapper.fromDto(dto)
        assertEquals(123456, grunnbeloep.value)
    }

    companion object {
        private fun tidsbegrensetVerdi() = TidsbegrensetVerdiDto(
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 1, 2),
            BigDecimal("123456.0")
        )

        private fun asCalendar(date: Date): Calendar {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+2"))
            calendar.time = date
            return calendar
        }
    }
}
