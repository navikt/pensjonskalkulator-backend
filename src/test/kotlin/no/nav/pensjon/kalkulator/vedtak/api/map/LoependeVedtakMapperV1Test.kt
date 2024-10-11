package no.nav.pensjon.kalkulator.vedtak.api.map

import no.nav.pensjon.kalkulator.vedtak.LoependeAlderspensjonDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakDetaljer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

import java.time.LocalDate

class LoependeVedtakMapperV1Test {

    @Test
    fun `map to dto`() {
        val vedtak = LoependeVedtak(
            alderspensjon = LoependeAlderspensjonDetaljer(
                grad = 100,
                fom = LocalDate.parse("2020-10-01")
            ),
            ufoeretrygd = LoependeVedtakDetaljer(
                grad = 50,
                fom = LocalDate.parse("2021-10-01")
            ),
            afpPrivat = LoependeVedtakDetaljer(
                grad = 75,
                fom = LocalDate.parse("2022-10-01")
            ),
            afpOffentlig = LoependeVedtakDetaljer(
                grad = 25,
                fom = LocalDate.parse("2023-10-01")
            )
        )

        val dto = LoependeVedtakMapperV1.toDto(vedtak)

        with(dto) {
            assertEquals(100, alderspensjon.grad)
            assertEquals(LocalDate.parse("2020-10-01"), alderspensjon.fom)
            assertEquals(50, ufoeretrygd.grad)
            assertEquals(LocalDate.parse("2021-10-01"), ufoeretrygd.fom)
            assertEquals(75, afpPrivat.grad)
            assertEquals(LocalDate.parse("2022-10-01"), afpPrivat.fom)
            assertEquals(25, afpOffentlig.grad)
            assertEquals(LocalDate.parse("2023-10-01"), afpOffentlig.fom)
        }
    }

    @Test
    fun `map ingen vedtak to dto`() {
        val vedtak = LoependeVedtak(
            alderspensjon = null,
            ufoeretrygd = null,
            afpPrivat = null,
            afpOffentlig = null
        )

        val dto = LoependeVedtakMapperV1.toDto(vedtak)

        with(dto) {
            assertFalse(alderspensjon.loepende)
            assertEquals(0, alderspensjon.grad)
            assertEquals(null, alderspensjon.fom)
            assertFalse(ufoeretrygd.loepende)
            assertEquals(0, ufoeretrygd.grad)
            assertEquals(null, ufoeretrygd.fom)
            assertFalse(afpPrivat.loepende)
            assertEquals(0, afpPrivat.grad)
            assertEquals(null, afpPrivat.fom)
            assertFalse(afpOffentlig.loepende)
            assertEquals(0, afpOffentlig.grad)
            assertEquals(null, afpOffentlig.fom)
        }
    }
}