package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.vedtak.*
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeUfoeregradDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakApDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto
import java.time.LocalDate

object LoependeVedtakMapper {

    fun fromDto(dto: PenLoependeVedtakDto) =
        LoependeVedtak(
            alderspensjon = dto.alderspensjon?.let {
                fromAlderspensjonDto(dto = it, uttaksgradFom = dto.gjeldendeUttaksgradFom)
            },
            fremtidigLoependeVedtakAp = fromFremtidigAlderspensjonDto(dto.alderspensjonIFremtid),
            ufoeretrygd = fromUfoeretrygdDto(dto.ufoeretrygd),
            afpPrivat = fromDto(dto.afpPrivat),
            afpOffentlig = null,
            pre2025OffentligAfp = fromDto(dto.afpOffentlig)
        )

    private fun fromDto(dto: PenGjeldendeVedtakDto?) = dto?.let { LoependeVedtakDetaljer(it.fraOgMed) }

    private fun fromAlderspensjonDto(dto: PenGjeldendeVedtakApDto, uttaksgradFom: LocalDate?) =
        LoependeAlderspensjonDetaljer(
            grad = dto.grad,
            fom = dto.fraOgMed,
            uttaksgradFom = uttaksgradFom ?: dto.fraOgMed,
            sivilstand = PenSivilstand.toInternalValue(dto.sivilstatus)
        )

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