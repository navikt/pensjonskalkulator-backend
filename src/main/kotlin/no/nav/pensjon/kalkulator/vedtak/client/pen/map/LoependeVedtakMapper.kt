package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.vedtak.LoependeAlderspensjonDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeUfoeretrygdDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakDetaljer
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeUfoeregradDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakApDto

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

    private fun fromAlderspensjonDto(dto: PenGjeldendeVedtakApDto?) = dto
        ?.let {
            LoependeAlderspensjonDetaljer(
                grad = it.grad,
                fom = it.fraOgMed,
                sivilstand = PenSivilstand.fromExternalValue(it.sivilstand)
            )
        }

    private fun fromUfoeretrygdDto(dto: PenGjeldendeUfoeregradDto?) =
        dto?.let { LoependeUfoeretrygdDetaljer(it.grad, it.fraOgMed) }
}