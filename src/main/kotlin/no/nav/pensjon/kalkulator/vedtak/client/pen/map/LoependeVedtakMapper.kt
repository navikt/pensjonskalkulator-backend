package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import no.nav.pensjon.kalkulator.vedtak.LoependeAlderspensjonDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeUfoeretrygdDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakDetaljer
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakMedGradDto

object LoependeVedtakMapper {

    fun fromDto(dto: PenLoependeVedtakDto): LoependeVedtak {
        return LoependeVedtak(
            alderspensjon = fromAlderspensjonDto(dto.alderspensjon),
            fremtidigLoependeVedtakAp = dto.fremtidigLoependeVedtakAp,
            ufoeretrygd = fromUfoeretrygdDto(dto.ufoeretrygd),
            afpPrivat = fromDto(dto.afpPrivat),
            afpOffentlig = null,
            afpOffentligForBrukereFoedtFoer1963 = fromDto(dto.afpOffentlig)
        )
    }

    private fun fromDto(dto: PenGjeldendeVedtakDto?) = dto?.let { LoependeVedtakDetaljer(it.fraOgMed) }

    private fun fromAlderspensjonDto(dto: PenGjeldendeVedtakMedGradDto?) = dto?.let { LoependeAlderspensjonDetaljer(it.grad, it.fraOgMed) }

    private fun fromUfoeretrygdDto(dto: PenGjeldendeVedtakMedGradDto?) = dto?.let { LoependeUfoeretrygdDetaljer(it.grad, it.fraOgMed) }
}