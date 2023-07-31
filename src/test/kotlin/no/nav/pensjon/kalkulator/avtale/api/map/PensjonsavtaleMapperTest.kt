package no.nav.pensjon.kalkulator.avtale.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.AvtaleKategori
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleDto
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtalerDto
import no.nav.pensjon.kalkulator.avtale.api.dto.SelskapDto
import no.nav.pensjon.kalkulator.avtale.api.dto.UtbetalingsperiodeDto
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.pensjonsavtaler
import org.junit.jupiter.api.Test

class PensjonsavtaleMapperTest {

    @Test
    fun `toDto maps pensjonsavtaler to DTO`() {
        PensjonsavtaleMapper.toDto(pensjonsavtaler(67)) shouldBe pensjonsavtalerDto(67)
    }

    @Test
    fun `toDto maps zero avtale-startalder to null`() {
        PensjonsavtaleMapper.toDto(pensjonsavtaler(0)) shouldBe pensjonsavtalerDto(null)
    }

    private fun pensjonsavtalerDto(startalder: Int?) =
        PensjonsavtalerDto(
            listOf(avtale(startalder)),
            listOf(selskap())
        )

    private fun avtale(startalder: Int?) =
        PensjonsavtaleDto(
            "produkt1",
            AvtaleKategori.INDIVIDUELL_ORDNING,
            startalder,
            77,
            listOf(utbetalingsperiode())
        )

    private fun utbetalingsperiode() =
        UtbetalingsperiodeDto(
            68,
            1,
            78,
            12,
            123000,
            100
        )

    private fun selskap() = SelskapDto("selskap1", true)
}
