package no.nav.pensjon.kalkulator.avtale.client.pen.map

import no.nav.pensjon.kalkulator.avtale.client.pen.dto.PensjonsavtaleDto
import no.nav.pensjon.kalkulator.avtale.client.pen.dto.PensjonsavtalerDto
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class PensjonsavtaleMapperTest {

    @Test
    fun `fromDto maps DTO to avtaler`() {
        val avtaler = PensjonsavtaleMapper.fromDto(avtalerDto())

        val avtale = avtaler.liste[0]
        assertEquals("avtale1", avtale.navn)
        assertEquals(LocalDate.of(1992, 3, 4), avtale.fom)
        assertEquals(LocalDate.of(2010, 11, 12), avtale.tom)
    }

    private companion object {
        private fun avtalerDto() = PensjonsavtalerDto(listOf(avtaleDto()))

        private fun avtaleDto() = PensjonsavtaleDto(
            "avtale1",
            LocalDate.of(1992, 3, 4),
            LocalDate.of(2010, 11, 12)
        )
    }
}
