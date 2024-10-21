package no.nav.pensjon.kalkulator.vedtak.api.map

import no.nav.pensjon.kalkulator.vedtak.LoependeAlderspensjonDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeUfoeretrygdDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakDetaljer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class LoependeVedtakMapperV2Test {

    @Test
    fun `map to dto`() {
        val vedtak = LoependeVedtak(
            alderspensjon = LoependeAlderspensjonDetaljer(
                grad = 100,
                fom = LocalDate.parse("2020-10-01")
            ),
            fremtidigLoependeVedtakAp = true,
            ufoeretrygd = LoependeUfoeretrygdDetaljer(
                grad = 50,
                fom = LocalDate.parse("2021-10-01")
            ),
            afpPrivat = LoependeVedtakDetaljer(
                fom = LocalDate.parse("2022-10-01")
            ),
            afpOffentlig = LoependeVedtakDetaljer(
                fom = LocalDate.parse("2023-10-01")
            )
        )

        val dto = LoependeVedtakMapperV2.toDto(vedtak)

        with(dto) {
            assertEquals(100, alderspensjon?.grad)
            assertEquals(LocalDate.parse("2020-10-01"), alderspensjon?.fom)
            assertEquals(50, ufoeretrygd.grad)
            assertEquals(LocalDate.parse("2022-10-01"), afpPrivat?.fom)
            assertEquals(LocalDate.parse("2023-10-01"), afpOffentlig?.fom)
        }
    }

    @Test
    fun `map ingen vedtak to dto`() {
        val vedtak = LoependeVedtak(
            alderspensjon = null,
            fremtidigLoependeVedtakAp = true,
            ufoeretrygd = null,
            afpPrivat = null,
            afpOffentlig = null
        )

        val dto = LoependeVedtakMapperV2.toDto(vedtak)

        with(dto) {
            assertNull(dto.alderspensjon)
            assertNotNull(dto.ufoeretrygd)
            assertEquals(0, ufoeretrygd.grad)
            assertNull(dto.afpPrivat)
            assertNull(dto.afpOffentlig)
        }
    }
}