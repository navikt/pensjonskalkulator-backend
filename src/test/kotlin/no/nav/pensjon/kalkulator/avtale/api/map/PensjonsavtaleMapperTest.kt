package no.nav.pensjon.kalkulator.avtale.api.map

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class PensjonsavtaleMapperTest {

    @Test
    fun `toDto maps avtaler to DTO`() {
        val dtos = PensjonsavtaleMapper.toDto(avtaler())

        val dto = dtos.avtaler[0]
        assertEquals("avtale1", dto.navn)
        assertEquals(LocalDate.of(1992, 3, 4), dto.fom)
        assertEquals(LocalDate.of(2010, 11, 12), dto.tom)
    }

    private companion object {
        private fun avtaler() = Pensjonsavtaler(listOf(pensjonsavtale()))

        private fun pensjonsavtale() = Pensjonsavtale(
            "avtale1",
            LocalDate.of(1992, 3, 4),
            LocalDate.of(2010, 11, 12)
        )
    }
}
