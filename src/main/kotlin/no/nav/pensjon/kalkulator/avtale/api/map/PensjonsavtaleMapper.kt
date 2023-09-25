package no.nav.pensjon.kalkulator.avtale.api.map

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.Selskap
import no.nav.pensjon.kalkulator.avtale.Utbetalingsperiode
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleDto
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtalerDto
import no.nav.pensjon.kalkulator.avtale.api.dto.SelskapDto
import no.nav.pensjon.kalkulator.avtale.api.dto.UtbetalingsperiodeDto

object PensjonsavtaleMapper {

    fun toDto(source: Pensjonsavtaler) =
        PensjonsavtalerDto(
            source.avtaler.map(::toAvtaleDto),
            source.utilgjengeligeSelskap.map(::toSelskapDto)
        )

    private fun toAvtaleDto(source: Pensjonsavtale) =
        PensjonsavtaleDto(
            source.produktbetegnelse,
            source.kategori,
            if (source.harStartalder) source.startalder else null,
            source.sluttalder,
            source.utbetalingsperioder.map(::toPeriodeDto)
        )

    private fun toPeriodeDto(source: Utbetalingsperiode) =
        UtbetalingsperiodeDto(
            source.start.aar,
            source.start.maaneder + 1,
            source.slutt?.aar,
            source.slutt?.let { it.maaneder + 1 },
            source.aarligUtbetalingForventet,
            source.grad.prosentsats
        )

    private fun toSelskapDto(source: Selskap) =
        SelskapDto(
            source.navn,
            source.heltUtilgjengelig
        )
}
