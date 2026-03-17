package no.nav.pensjon.kalkulator.vedtak.api.v1.acl

import no.nav.pensjon.kalkulator.vedtak.*
import java.math.BigDecimal

object VedtakResultMapper {

    fun toDto(source: VedtakSamling) =
        VedtakV1Samling(
            harVedtak = source.hasContent(),
            loependeAlderspensjon = source.loependeAlderspensjon?.let(::loependeAlderspensjon),
            fremtidigAlderspensjon = source.fremtidigAlderspensjon?.let(::fremtidigAlderspensjon),
            ufoeretrygdgrad = source.ufoeretrygd?.grad,
            privatAfpFom = source.privatAfp?.fom,
            tidsbegrensetOffentligAfpFom = source.pre2025OffentligAfp?.fom
        )

    private fun loependeAlderspensjon(source: LoependeAlderspensjon) =
        VedtakV1LoependeAlderspensjon(
            grad = source.grad,
            fom = source.fom,
            uttaksgradFom = source.uttaksgradFom ?: source.fom,
            sisteUtbetaling = source.utbetalingSisteMaaned?.let(::utbetaling),
            sivilstatus = VedtakV1Sivilstatus.fromInternalValue(source.sivilstand)
        )

    private fun fremtidigAlderspensjon(source: FremtidigAlderspensjon) =
        VedtakV1Alderspensjonsuttak(
            grad = source.grad,
            fom = source.fom
        )

    private fun utbetaling(source: Utbetaling) =
        VedtakV1Utbetaling(
            beloep = source.beloep ?: BigDecimal.ZERO,
            utbetalingsdato = source.posteringsdato
        )
}
