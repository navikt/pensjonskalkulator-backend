package no.nav.pensjon.kalkulator.avtale.api.map

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.Selskap
import no.nav.pensjon.kalkulator.avtale.Utbetalingsperiode
import no.nav.pensjon.kalkulator.avtale.api.dto.*

object PensjonsavtaleResultMapperV2 {

    fun toDtoV2(source: Pensjonsavtaler) =
        PensjonsavtaleResultV2(
            avtaler = source.avtaler.map(::avtale),
            utilgjengeligeSelskap = source.utilgjengeligeSelskap.map(::selskap)
        )

    private fun avtale(source: Pensjonsavtale) =
        PensjonsavtaleV2(
            produktbetegnelse = source.produktbetegnelse,
            kategori = AvtaleKategoriV2.fromInternalValue(source.kategori),
            startAar = source.startAar,
            sluttAar = source.sluttAar,
            utbetalingsperioder = source.utbetalingsperioder.map(::periode)
        )

    private fun periode(source: Utbetalingsperiode) =
        UtbetalingsperiodeV2(
            startAlder = source.startAlder,
            sluttAlder = source.sluttAlder,
            aarligUtbetaling = source.aarligUtbetalingForventet,
            grad = source.grad.prosentsats
        )

    private fun selskap(source: Selskap) =
        SelskapV2(
            navn = source.navn,
            heltUtilgjengelig = source.heltUtilgjengelig
        )
}
