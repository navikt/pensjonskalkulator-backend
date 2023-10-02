package no.nav.pensjon.kalkulator.avtale.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.AvtaleKategori
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.api.dto.*
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.pensjonsavtaler
import org.junit.jupiter.api.Test

class PensjonsavtaleMapperTest {

    @Test
    fun `fromDto maps data transfer object to domain object (pensjonsavtale specification)`() {
        PensjonsavtaleMapper.fromDto(pensjonsavtaleIngressSpecDto()) shouldBe pensjonsavtaleSpec()
    }

    @Test
    fun `toDto maps domain object (pensjonsavtaler) to data transfer object`() {
        PensjonsavtaleMapper.toDto(pensjonsavtaler(67)) shouldBe pensjonsavtalerDto(67)
    }

    @Test
    fun `toDto maps zero avtale-startalder to null`() {
        PensjonsavtaleMapper.toDto(pensjonsavtaler(0)) shouldBe pensjonsavtalerDto(null)
    }

    private companion object {
        private fun pensjonsavtaleIngressSpecDto() =
            PensjonsavtaleIngressSpecDto(
                aarligInntektFoerUttak = 1,
                uttaksperioder = emptyList(),
                antallInntektsaarEtterUttak = 2,
                harAfp = true // this value will be ignored in mapping
            )

        private fun pensjonsavtaleSpec() =
            PensjonsavtaleSpec(
                aarligInntektFoerUttak = 1,
                uttaksperioder = emptyList(),
                antallInntektsaarEtterUttak = 2
            )

        private fun pensjonsavtalerDto(startalder: Int?) =
            PensjonsavtalerDto(
                avtaler = listOf(avtale(startalder)),
                utilgjengeligeSelskap = listOf(selskap())
            )

        private fun avtale(startalder: Int?) =
            PensjonsavtaleDto(
                produktbetegnelse = "produkt1",
                kategori = AvtaleKategori.INDIVIDUELL_ORDNING,
                startAar = startalder,
                sluttAar = 77,
                utbetalingsperioder = listOf(utbetalingsperiode())
            )

        private fun utbetalingsperiode() =
            UtbetalingsperiodeDto(
                startAlder = Alder(68, 1),
                sluttAlder = Alder(78, 11),
                aarligUtbetaling = 123000,
                grad = 100
            )

        private fun selskap() = SelskapDto(navn = "selskap1", heltUtilgjengelig = true)
    }
}
