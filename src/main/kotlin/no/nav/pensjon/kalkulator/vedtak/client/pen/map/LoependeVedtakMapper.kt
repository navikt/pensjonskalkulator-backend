package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import no.nav.pensjon.kalkulator.vedtak.LopenedeVedtakDetaljer
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
        )
    }

    private fun fromDto(dto: PenLopenedeVedtakMedGradDto?) = dto?.let { LopenedeVedtakDetaljer(it.grad, it.fraOgMed) }
}