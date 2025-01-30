package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeUfoeregradDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakApDto
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class LoependeVedtakMapperTest {

    @Test
    fun `Map from PEN og ignorer gammel afpOffentlig`() {
        val dto = PenLoependeVedtakDto(
            alderspensjon = PenGjeldendeVedtakApDto(1, LocalDate.of(2021, 1, 1), sivilstand = "UGIF"),
            alderspensjonIFremtid = PenGjeldendeVedtakApDto(3, LocalDate.of(2022, 1, 1), sivilstand = "GIFT"),
            ufoeretrygd = PenGjeldendeUfoeregradDto(2, LocalDate.of(2021, 1, 1)),
            afpPrivat = PenGjeldendeVedtakDto(LocalDate.of(2021, 1, 1)),
            afpOffentlig = PenGjeldendeVedtakDto(LocalDate.of(2021, 1, 1)),
        )

        val result = LoependeVedtakMapper.fromDto(dto)

        assertEquals(1, result.alderspensjon?.grad)
        assertEquals(LocalDate.of(2021, 1, 1), result.alderspensjon?.fom)
        assertEquals("UGIFT", result.alderspensjon?.sivilstand?.name)
        assertNotNull(result.fremtidigLoependeVedtakAp)
        assertEquals(3, result.fremtidigLoependeVedtakAp?.grad)
        assertEquals(LocalDate.of(2022, 1, 1), result.fremtidigLoependeVedtakAp?.fom)
        assertEquals("GIFT", result.fremtidigLoependeVedtakAp?.sivilstand?.name)
        assertEquals(2, result.ufoeretrygd?.grad)
        assertEquals(LocalDate.of(2021, 1, 1), result.ufoeretrygd?.fom)
        assertEquals(LocalDate.of(2021, 1, 1), result.afpPrivat?.fom)
        assertNull(result.afpOffentlig)
        assertEquals(LocalDate.of(2021, 1, 1), result.afpOffentligForBrukereFoedtFoer1963?.fom)
    }
}