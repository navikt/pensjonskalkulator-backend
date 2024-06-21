package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.PenUttakAlder
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.PenUttaksalderResult
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PenUttaksalderResultMapperTest {

    @Test
    fun `'fromDto' maps data transfer object to domain object`() {
        PenUttaksalderResultMapper.fromDto(
            PenUttaksalderResult(
                alder = PenUttakAlder(aar = 62, maaneder = 11),
                dato = LocalDate.of(2024, 6, 15)
            )
        ) shouldBe
                Alder(aar = 62, maaneder = 11)
    }
}
