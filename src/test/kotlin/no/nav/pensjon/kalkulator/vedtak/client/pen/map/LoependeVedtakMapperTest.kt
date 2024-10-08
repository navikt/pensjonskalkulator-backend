package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLopenedeVedtakMedGradDto
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class LoependeVedtakMapperTest {

    @Test
    fun `Map from PEN og ignorer gammel afpOffentlig`() {
        val dto = PenLoependeVedtakDto(
            alderspensjon = PenLopenedeVedtakMedGradDto(1, LocalDate.of(2021, 1, 1)),
            ufoeretrygd = PenLopenedeVedtakMedGradDto(2, LocalDate.of(2021, 1, 1)),
            afpPrivat = PenLopenedeVedtakMedGradDto(3, LocalDate.of(2021, 1, 1)),
            afpOffentlig = PenLopenedeVedtakMedGradDto(4, LocalDate.of(2021, 1, 1)),
        )

        val result = LoependeVedtakMapper.fromDto(dto)

        assertEquals(1, result.alderspensjon?.grad)
        assertEquals(LocalDate.of(2021, 1, 1), result.alderspensjon?.fom)
        assertEquals(2, result.ufoeretrygd?.grad)
        assertEquals(LocalDate.of(2021, 1, 1), result.ufoeretrygd?.fom)
        assertEquals(3, result.afpPrivat?.grad)
        assertEquals(LocalDate.of(2021, 1, 1), result.afpPrivat?.fom)
        assertNull(result.afpOffentlig)
        assertEquals(4, result.afpOffentligForBrukereFoedtFoer1963?.grad)
        assertEquals(LocalDate.of(2021, 1, 1), result.afpOffentligForBrukereFoedtFoer1963?.fom)
    }
}