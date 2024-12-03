package no.nav.pensjon.kalkulator.avtale.api.map

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.Selskap
import no.nav.pensjon.kalkulator.avtale.Utbetalingsperiode
import no.nav.pensjon.kalkulator.avtale.api.dto.*

object PensjonsavtaleResultMapperV3 {

    fun toDtoV3(source: Pensjonsavtaler) =
        PensjonsavtaleResultV3(
            avtaler = source.avtaler.map(::avtale),
            utilgjengeligeSelskap = source.utilgjengeligeSelskap.map(::selskap)
        )

    private fun avtale(source: Pensjonsavtale) =
        PensjonsavtaleV3(
            produktbetegnelse = source.produktbetegnelse,
            kategori = AvtaleKategoriV3.fromInternalValue(source.kategori),
            startAar = source.startAar,
            sluttAar = source.sluttAar,
            utbetalingsperioder = source.utbetalingsperioder.map(::periode)
        )

    private fun periode(source: Utbetalingsperiode) =
        UtbetalingsperiodeV3(
            startAlder = source.startAlder,
            sluttAlder = source.sluttAlder,
            aarligUtbetaling = source.aarligUtbetalingForventet,
            grad = source.grad.prosentsats
        )

    private fun selskap(source: Selskap) =
        SelskapV3(
            navn = source.navn,
            heltUtilgjengelig = source.heltUtilgjengelig
        )
}
