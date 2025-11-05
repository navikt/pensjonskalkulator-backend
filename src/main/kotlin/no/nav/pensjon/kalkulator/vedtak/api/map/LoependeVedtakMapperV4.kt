package no.nav.pensjon.kalkulator.vedtak.api.map

import no.nav.pensjon.kalkulator.vedtak.*
import no.nav.pensjon.kalkulator.vedtak.api.dto.*
import java.math.BigDecimal

object LoependeVedtakMapperV4 {

    fun toDto(source: VedtakSamling) = LoependeVedtakV4(
        harLoependeVedtak = source.hasContent(),
        alderspensjon = source.loependeAlderspensjon?.let(::loependeAlderspensjon),
        fremtidigAlderspensjon = source.fremtidigAlderspensjon?.let(::fremtidigAlderspensjon),
        ufoeretrygd = source.ufoeretrygd?.let(::ufoeretrygd) ?: UfoeretrygdDetaljerV4(grad = 0),
        afpPrivat = source.privatAfp?.let(::loependeEntitet),
        afpOffentlig = null, //TODO remove?
        pre2025OffentligAfp = source.pre2025OffentligAfp?.let(::loependeEntitet),
    )

    private fun loependeAlderspensjon(source: LoependeAlderspensjon) =
        AlderspensjonDetaljerV4(
            grad = source.grad,
            fom = source.fom,
            uttaksgradFom = source.uttaksgradFom ?: source.fom,
            sisteUtbetaling = source.utbetalingSisteMaaned?.let(::utbetaling),
            sivilstand = SivilstandV4.fromInternalValue(source.sivilstand)
        )

    private fun fremtidigAlderspensjon(source: FremtidigAlderspensjon) =
        FremtidigAlderspensjonDetaljerV4(
            grad = source.grad,
            fom = source.fom,
        )

    private fun ufoeretrygd(source: LoependeUfoeretrygd) =
        UfoeretrygdDetaljerV4(grad = source.grad)

    private fun loependeEntitet(source: LoependeEntitet) =
        LoependeFraV4(fom = source.fom)

    private fun utbetaling(source: Utbetaling) =
        UtbetalingV4(
            beloep = source.beloep ?: BigDecimal.ZERO,
            utbetalingsdato = source.posteringsdato
        )
}
