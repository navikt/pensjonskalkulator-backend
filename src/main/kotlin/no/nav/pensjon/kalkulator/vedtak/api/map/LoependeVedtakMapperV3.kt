package no.nav.pensjon.kalkulator.vedtak.api.map

import no.nav.pensjon.kalkulator.vedtak.LoependeAlderspensjonDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeUfoeretrygdDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakDetaljer
import no.nav.pensjon.kalkulator.vedtak.api.dto.*
import java.math.BigDecimal

object LoependeVedtakMapperV3 {

    fun toDto(vedtak: LoependeVedtak) = LoependeVedtakV3(
        alderspensjon = toAlderspensjonDetaljerV3Dto(vedtak.alderspensjon),
        harFremtidigLoependeVedtak = vedtak.fremtidigLoependeVedtakAp,
        ufoeretrygd = toUfoeretrygdDetaljerV3Dto(vedtak.ufoeretrygd),
        afpPrivat = toLoependeFraV3Dto(vedtak.afpPrivat),
        afpOffentlig = toLoependeFraV3Dto(vedtak.afpOffentlig),
    )

    private fun toAlderspensjonDetaljerV3Dto(alderspensjon: LoependeAlderspensjonDetaljer?) = alderspensjon?.let {
        AlderspensjonDetaljerV3(
            grad = alderspensjon.grad,
            fom = alderspensjon.fom,
            sisteUtbetaling = alderspensjon.utbetalingSisteMaaned?.let {
                UtbetalingV3(
                    beloep = it.beloep ?: BigDecimal(0),
                    utbetalingsdato = it.posteringsdato,
                )
            },
            sivilstand = SivilstandV3.fromInternalValue(alderspensjon.sivilstand)
        )
    }

    private fun toUfoeretrygdDetaljerV3Dto(ufoeretrygd: LoependeUfoeretrygdDetaljer?) : UfoeretrygdDetaljerV3 = UfoeretrygdDetaljerV3(grad = ufoeretrygd?.grad?: 0)

    private fun toLoependeFraV3Dto(loependeVedtakDetaljer: LoependeVedtakDetaljer?) = loependeVedtakDetaljer?.fom?.let { LoependeFraV3(fom = it) }

}