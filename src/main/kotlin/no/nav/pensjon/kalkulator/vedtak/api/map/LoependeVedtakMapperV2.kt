package no.nav.pensjon.kalkulator.vedtak.api.map

import no.nav.pensjon.kalkulator.vedtak.LoependeAlderspensjonDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakDetaljer
import no.nav.pensjon.kalkulator.vedtak.api.dto.*

object LoependeVedtakMapperV2 {

    fun toDto(vedtak: LoependeVedtak) = LoependeVedtakV2(
        alderspensjon = toAlderspensjonDetaljerV2Dto(vedtak.alderspensjon),
        ufoeretrygd = toUfoeretrygdDetaljerV2Dto(vedtak.ufoeretrygd),
        afpPrivat = toLoependeFraV2Dto(vedtak.afpPrivat),
        afpOffentlig = toLoependeFraV2Dto(vedtak.afpOffentlig),
    )

    private fun toAlderspensjonDetaljerV2Dto(alderspensjon: LoependeAlderspensjonDetaljer?) = alderspensjon?.let {
        AlderspensjonDetaljerV2(
            grad = alderspensjon.grad,
            fom = alderspensjon.fom,
            sisteUtbetaling = alderspensjon.utbetalingSisteMaaned?.let {
                UtbetalingSisteMaanedV2(
                    beloep = it.beloep?.toInt() ?: 0, //TODO RoundingMode.HALF_EVEN
                    utbetalingsdato = it.posteringsdato,
                )
            }
        )
    }

    private fun toUfoeretrygdDetaljerV2Dto(ufoeretrygd: LoependeVedtakDetaljer?) : UfoeretrygdDetaljerV2 = UfoeretrygdDetaljerV2(grad = ufoeretrygd?.grad?: 0)

    private fun toLoependeFraV2Dto(loependeVedtakDetaljer: LoependeVedtakDetaljer?) = loependeVedtakDetaljer?.fom?.let { LoependeFraV2(fom = it,) }

}