package no.nav.pensjon.kalkulator.avtale.api.map

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.Utbetalingsperiode
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleDto
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtalerDto
import no.nav.pensjon.kalkulator.avtale.api.dto.UtbetalingsperiodeDto

object PensjonsavtaleMapper {

    fun toDto(source: Pensjonsavtaler) = PensjonsavtalerDto(source.liste.map(::pensjonsavtale))

    fun toDto(source: Pensjonsavtale) = PensjonsavtalerDto(listOf(pensjonsavtale(source)))

    private fun pensjonsavtale(source: Pensjonsavtale) =
        PensjonsavtaleDto(
            source.produktbetegnelse,
            source.kategori,
            source.startAlder,
            source.sluttAlder,
            toDto(source.utbetalingsperiode)
        )

    private fun toDto(source: Utbetalingsperiode) =
        UtbetalingsperiodeDto(
            source.start.aar,
            source.start.maaned,
            source.slutt?.aar,
            source.slutt?.maaned,
            source.aarligUtbetaling,
            source.grad
        )
}
