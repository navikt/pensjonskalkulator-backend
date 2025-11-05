package no.nav.pensjon.kalkulator.vedtak.api.map

import no.nav.pensjon.kalkulator.vedtak.*
import no.nav.pensjon.kalkulator.vedtak.api.dto.*
import java.math.BigDecimal

object LoependeVedtakMapperV4 {

    fun toDto(vedtak: LoependeVedtak) = LoependeVedtakV4(
        harLoependeVedtak = hasContent(vedtak),
        alderspensjon = vedtak.alderspensjon?.let(::toAlderspensjonDetaljerV4Dto),
        fremtidigAlderspensjon = vedtak.fremtidigLoependeVedtakAp?.let(::toAlderspensjonDetaljerV4Dto),
        ufoeretrygd = vedtak.ufoeretrygd?.let(::toUfoeretrygdDetaljerV4Dto) ?: UfoeretrygdDetaljerV4(grad = 0),
        afpPrivat = vedtak.afpPrivat?.let(::toLoependeFraV4Dto),
        afpOffentlig = vedtak.afpOffentlig?.let(::toLoependeFraV4Dto),
        pre2025OffentligAfp = vedtak.pre2025OffentligAfp?.let(::toLoependeFraV4Dto),
    )

    private fun hasContent(vedtak: LoependeVedtak): Boolean =
        vedtak.alderspensjon != null &&
                vedtak.fremtidigLoependeVedtakAp != null &&
                vedtak.ufoeretrygd != null &&
                vedtak.afpPrivat != null &&
                vedtak.afpOffentlig != null &&
                vedtak.pre2025OffentligAfp != null

    private fun toAlderspensjonDetaljerV4Dto(alderspensjon: LoependeAlderspensjonDetaljer) =
        AlderspensjonDetaljerV4(
            grad = alderspensjon.grad,
            fom = alderspensjon.fom,
            uttaksgradFom = alderspensjon.uttaksgradFom ?: alderspensjon.fom,
            sisteUtbetaling = alderspensjon.utbetalingSisteMaaned?.let(::toUtbetalingV4),
            sivilstand = SivilstandV4.fromInternalValue(alderspensjon.sivilstand)
        )

    private fun toAlderspensjonDetaljerV4Dto(alderspensjon: FremtidigAlderspensjonDetaljer) =
        FremtidigAlderspensjonDetaljerV4(
            grad = alderspensjon.grad,
            fom = alderspensjon.fom,
        )

    private fun toUfoeretrygdDetaljerV4Dto(ufoeretrygd: LoependeUfoeretrygdDetaljer) =
        UfoeretrygdDetaljerV4(grad = ufoeretrygd.grad)

    private fun toLoependeFraV4Dto(vedtak: LoependeVedtakDetaljer) =
        LoependeFraV4(fom = vedtak.fom)

    private fun toUtbetalingV4(utbetaling: Utbetaling) =
        UtbetalingV4(
            beloep = utbetaling.beloep ?: BigDecimal.ZERO,
            utbetalingsdato = utbetaling.posteringsdato
        )
}
