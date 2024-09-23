package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import no.nav.pensjon.kalkulator.vedtak.Grad
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto

object LoependeVedtakMapper {

    fun fromDto(dto: PenLoependeVedtakDto): LoependeVedtak {
        return LoependeVedtak(
            alderspensjon = dto.alderspensjon?.let { Grad(grad = it.grad) },
            ufoeretrygd = dto.ufoeretrygd?.let { Grad(grad = it.grad) },
            afpPrivat = dto.afpPrivat?.let { Grad(grad = it.grad) },
            afpOffentlig = dto.afpOffentlig?.let { Grad(grad = it.grad) },
        )
    }
}