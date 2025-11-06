package no.nav.pensjon.kalkulator.vedtak.api.map

import no.nav.pensjon.kalkulator.vedtak.LoependeAlderspensjon
import no.nav.pensjon.kalkulator.vedtak.LoependeUfoeretrygd
import no.nav.pensjon.kalkulator.vedtak.VedtakSamling
import no.nav.pensjon.kalkulator.vedtak.LoependeEntitet
import no.nav.pensjon.kalkulator.vedtak.api.dto.*
import java.math.BigDecimal

object LoependeVedtakMapperV3 {

    fun toDto(vedtak: VedtakSamling) = LoependeVedtakV3(
        alderspensjon = toAlderspensjonDetaljerV3Dto(vedtak.loependeAlderspensjon),
        harFremtidigLoependeVedtak = vedtak.fremtidigAlderspensjon != null,
        ufoeretrygd = toUfoeretrygdDetaljerV3Dto(vedtak.ufoeretrygd),
        afpPrivat = toLoependeFraV3Dto(vedtak.privatAfp),
    )

    private fun toAlderspensjonDetaljerV3Dto(alderspensjon: LoependeAlderspensjon?) = alderspensjon?.let {
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

    private fun toUfoeretrygdDetaljerV3Dto(ufoeretrygd: LoependeUfoeretrygd?) : UfoeretrygdDetaljerV3 = UfoeretrygdDetaljerV3(grad = ufoeretrygd?.grad?: 0)

    private fun toLoependeFraV3Dto(loependeEntitet: LoependeEntitet?) = loependeEntitet?.fom?.let { LoependeFraV3(fom = it) }

}