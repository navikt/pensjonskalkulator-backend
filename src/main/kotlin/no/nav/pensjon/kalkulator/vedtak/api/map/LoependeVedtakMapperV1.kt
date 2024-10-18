package no.nav.pensjon.kalkulator.vedtak.api.map

import no.nav.pensjon.kalkulator.vedtak.LoependeAlderspensjonDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeUfoeretrygdDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak
import no.nav.pensjon.kalkulator.vedtak.api.dto.LoependeVedtakDetaljerV1
import no.nav.pensjon.kalkulator.vedtak.api.dto.LoependeVedtakV1

object LoependeVedtakMapperV1 {

    fun toDto(vedtak: LoependeVedtak) = LoependeVedtakV1(
        alderspensjon = vedtak.alderspensjon?.let { toDto(it) } ?: toDtoIkkeLoepende(),
        ufoeretrygd = vedtak.ufoeretrygd?.let { toDto(it) } ?: toDtoIkkeLoepende(),
        afpPrivat = vedtak.afpPrivat?.let { toDto(it) } ?: toDtoIkkeLoepende(),
        afpOffentlig = vedtak.afpOffentlig?.let { toDto(it) } ?: toDtoIkkeLoepende(),
    )

    fun toDto(loependeVedtakDetaljer: LoependeVedtakDetaljer) = LoependeVedtakDetaljerV1(
        loepende = true,
        grad = 100,
        fom = loependeVedtakDetaljer.fom,
    )

    fun toDto(loependeAlderspensjonDetaljer: LoependeAlderspensjonDetaljer) = LoependeVedtakDetaljerV1(
        loepende = true,
        grad = loependeAlderspensjonDetaljer.grad,
        fom = loependeAlderspensjonDetaljer.fom,
    )

    fun toDto(loependeUfoeretrygdDetaljer: LoependeUfoeretrygdDetaljer) = LoependeVedtakDetaljerV1(
        loepende = true,
        grad = loependeUfoeretrygdDetaljer.grad,
        fom = loependeUfoeretrygdDetaljer.fom,
    )

    private fun toDtoIkkeLoepende() = LoependeVedtakDetaljerV1()
}