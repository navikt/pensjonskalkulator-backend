package no.nav.pensjon.kalkulator.vedtak.api.map

import no.nav.pensjon.kalkulator.vedtak.Grad
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

    fun toDto(grad: Grad) = LoependeVedtakDetaljerV1(
        loepende = true,
        grad = grad.grad
    )

    fun toDtoIkkeLoepende() = LoependeVedtakDetaljerV1(loepende = false)
}