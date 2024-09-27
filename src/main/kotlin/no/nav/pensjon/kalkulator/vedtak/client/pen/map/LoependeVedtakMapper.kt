package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLopenedeVedtakMedGradDto

object LoependeVedtakMapper {

    fun fromDto(dto: PenLoependeVedtakDto): LoependeVedtak {
        return LoependeVedtak(
            alderspensjon = fromDto(dto.alderspensjon),
            ufoeretrygd = fromDto(dto.ufoeretrygd),
            afpPrivat = fromDto(dto.afpPrivat),
            afpOffentlig = null,
            afpOffentligForBrukereFoedtFoer1963 = fromDto(dto.afpOffentlig)
        )
    }

    private fun fromDto(dto: PenLopenedeVedtakMedGradDto?) = dto?.let { LoependeVedtakDetaljer(it.grad, it.fraOgMed) }
}