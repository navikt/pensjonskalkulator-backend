package no.nav.pensjon.kalkulator.vedtak.api.map

import no.nav.pensjon.kalkulator.vedtak.LopenedeVedtakDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak
import no.nav.pensjon.kalkulator.vedtak.api.dto.LoependeVedtakDetaljerV1
import no.nav.pensjon.kalkulator.vedtak.api.dto.LoependeVedtakV1

object LoependeVedtakMapperV1 {

    fun toDto(vedtak: LoependeVedtak) = LoependeVedtakV1(
        alderspensjon = vedtak.alderspensjon?.let { toDto(it) } ?: toDtoIkkeLoepende(),
        ufoeretrygd = vedtak.ufoeretrygd?.let { toDto(it) } ?: toDtoIkkeLoepende(),
        afpPrivat = vedtak.afpPrivat?.let { toDto(it) } ?: toDtoIkkeLoepende(),
        afpOffentlig = toDtoIkkeLoepende(),
    )

    fun toDto(lopenedeVedtakDetaljer: LopenedeVedtakDetaljer) = LoependeVedtakDetaljerV1(
        loepende = true,
        grad = lopenedeVedtakDetaljer.grad,
        fom = lopenedeVedtakDetaljer.fom,
    )

    fun toDtoIkkeLoepende() = LoependeVedtakDetaljerV1()
}