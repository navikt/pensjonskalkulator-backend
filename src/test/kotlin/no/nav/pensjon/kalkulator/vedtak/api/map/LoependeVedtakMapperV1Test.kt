package no.nav.pensjon.kalkulator.vedtak.api.map

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.vedtak.*
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
                fom = LocalDate.parse("2020-10-01"),
                sivilstand = Sivilstand.GIFT,
            ),
            fremtidigLoependeVedtakAp = FremtidigAlderspensjonDetaljer(
                grad = 10,
                fom = LocalDate.parse("2021-12-01"),
                sivilstand = Sivilstand.SKILT
            ),
            ufoeretrygd = LoependeUfoeretrygdDetaljer(
                grad = 50,
                fom = LocalDate.parse("2021-10-01")
            ),
            afpPrivat = LoependeVedtakDetaljer(
                fom = LocalDate.parse("2022-10-01")
            ),
            afpOffentlig = LoependeVedtakDetaljer(
                fom = LocalDate.parse("2023-10-01")
            ),
            gjeldendeUttaksgradFom = null
        )

        val dto = LoependeVedtakMapperV1.toDto(vedtak)

        with(dto) {
            assertEquals(100, alderspensjon.grad)
            assertEquals(LocalDate.parse("2020-10-01"), alderspensjon.fom)
            assertEquals(50, ufoeretrygd.grad)
            assertEquals(LocalDate.parse("2021-10-01"), ufoeretrygd.fom)
            assertEquals(100, afpPrivat.grad)
            assertEquals(LocalDate.parse("2022-10-01"), afpPrivat.fom)
            assertEquals(100, afpOffentlig.grad)
            assertEquals(LocalDate.parse("2023-10-01"), afpOffentlig.fom)
        }
    }

    @Test
    fun `map ingen vedtak to dto`() {
        val vedtak = LoependeVedtak(
            alderspensjon = null,
            fremtidigLoependeVedtakAp = FremtidigAlderspensjonDetaljer(
                grad = 10,
                fom = LocalDate.parse("2021-12-01"),
                sivilstand = Sivilstand.SKILT
            ),
            ufoeretrygd = null,
            afpPrivat = null,
            afpOffentlig = null,
            gjeldendeUttaksgradFom = null
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