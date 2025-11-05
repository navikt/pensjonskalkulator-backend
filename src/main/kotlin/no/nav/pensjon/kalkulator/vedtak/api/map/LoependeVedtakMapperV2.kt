package no.nav.pensjon.kalkulator.vedtak.api.map

import no.nav.pensjon.kalkulator.vedtak.LoependeAlderspensjon
import no.nav.pensjon.kalkulator.vedtak.LoependeUfoeretrygd
import no.nav.pensjon.kalkulator.vedtak.VedtakSamling
import no.nav.pensjon.kalkulator.vedtak.LoependeEntitet
import no.nav.pensjon.kalkulator.vedtak.api.dto.*
import java.math.BigDecimal

object LoependeVedtakMapperV2 {

    fun toDto(vedtak: VedtakSamling) = LoependeVedtakV2(
        alderspensjon = toAlderspensjonDetaljerV2Dto(vedtak.loependeAlderspensjon),
        harFremtidigLoependeVedtak = vedtak.fremtidigAlderspensjon != null,
        ufoeretrygd = toUfoeretrygdDetaljerV2Dto(vedtak.ufoeretrygd),
        afpPrivat = toLoependeFraV2Dto(vedtak.privatAfp),
    )

    private fun toAlderspensjonDetaljerV2Dto(alderspensjon: LoependeAlderspensjon?) = alderspensjon?.let {
        AlderspensjonDetaljerV2(
            grad = alderspensjon.grad,
            fom = alderspensjon.fom,
            sisteUtbetaling = alderspensjon.utbetalingSisteMaaned?.let {
                UtbetalingV2(
                    beloep = it.beloep ?: BigDecimal(0),
                    utbetalingsdato = it.posteringsdato,
                )
            }
        )
    }

    private fun toUfoeretrygdDetaljerV2Dto(ufoeretrygd: LoependeUfoeretrygd?) : UfoeretrygdDetaljerV2 = UfoeretrygdDetaljerV2(grad = ufoeretrygd?.grad?: 0)

    private fun toLoependeFraV2Dto(loependeEntitet: LoependeEntitet?) = loependeEntitet?.fom?.let { LoependeFraV2(fom = it) }

}