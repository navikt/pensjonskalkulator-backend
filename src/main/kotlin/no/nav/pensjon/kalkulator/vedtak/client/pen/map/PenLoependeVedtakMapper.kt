package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.vedtak.*
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeUfoeregradDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakApDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto
import java.time.LocalDate

/**
 * Maps from data transfer object (DTO) to domain object.
 * The DTO represents 'løpende vedtak' received from PEN.
 */
object PenLoependeVedtakMapper {

    fun fromDto(source: PenLoependeVedtakDto) =
        VedtakSamling(
            loependeAlderspensjon = source.alderspensjon?.let {
                loependeAlderspensjon(source = it, uttaksgradFom = source.gjeldendeUttaksgradFom)
            },
            fremtidigAlderspensjon = fremtidigUttakgradsendring(source),
            ufoeretrygd = source.ufoeretrygd?.let(::ufoeretrygd),
            privatAfp = source.afpPrivat?.let(::vedtak),
            pre2025OffentligAfp = source.afpOffentlig?.let(::vedtak)
        )

    private fun vedtak(source: PenGjeldendeVedtakDto) =
        LoependeEntitet(fom = source.fraOgMed)

    private fun loependeAlderspensjon(source: PenGjeldendeVedtakApDto, uttaksgradFom: LocalDate?) =
        LoependeAlderspensjon(
            grad = source.grad,
            fom = source.fraOgMed,
            uttaksgradFom = uttaksgradFom ?: source.fraOgMed,
            sivilstand = PenSivilstand.toInternalValue(source.sivilstatus)
        )

    private fun fremtidigUttakgradsendring(source: PenLoependeVedtakDto) =
        source.alderspensjonIFremtid
            ?.takeIf { it.grad != source.alderspensjon?.grad }
            ?.let { FremtidigAlderspensjon(it.grad, it.fraOgMed, PenSivilstand.toInternalValue(it.sivilstatus)) }

    private fun ufoeretrygd(source: PenGjeldendeUfoeregradDto) =
        LoependeUfoeretrygd(
            grad = source.grad,
            fom = source.fraOgMed
        )
}