package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.vedtak.*
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeUfoeregradDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakApDto

object LoependeVedtakMapper {

    fun fromDto(dto: PenLoependeVedtakDto): LoependeVedtak {
        return LoependeVedtak(
            alderspensjon = fromAlderspensjonDto(dto.alderspensjon),
            fremtidigLoependeVedtakAp = fromFremtidigAlderspensjonDto(dto.alderspensjonIFremtid),
            ufoeretrygd = fromUfoeretrygdDto(dto.ufoeretrygd),
            afpPrivat = fromDto(dto.afpPrivat),
            afpOffentlig = null,
            pre2025OffentligAfp = fromDto(dto.afpOffentlig)
        )
    }

    private fun fromDto(dto: PenGjeldendeVedtakDto?) = dto?.let { LoependeVedtakDetaljer(it.fraOgMed) }

    private fun fromAlderspensjonDto(dto: PenGjeldendeVedtakApDto?) = dto
        ?.let {
            LoependeAlderspensjonDetaljer(
                grad = it.grad,
                fom = it.fraOgMed,
                sivilstand = PenSivilstand.toInternalValue(it.sivilstatus)
            )
        }

    private fun fromFremtidigAlderspensjonDto(dto: PenGjeldendeVedtakApDto?) = dto
        ?.let {
            FremtidigAlderspensjonDetaljer(
                grad = it.grad,
                fom = it.fraOgMed,
                sivilstand = PenSivilstand.toInternalValue(it.sivilstatus)
            )
        }


    private fun fromUfoeretrygdDto(dto: PenGjeldendeUfoeregradDto?) =
        dto?.let { LoependeUfoeretrygdDetaljer(it.grad, it.fraOgMed) }
}