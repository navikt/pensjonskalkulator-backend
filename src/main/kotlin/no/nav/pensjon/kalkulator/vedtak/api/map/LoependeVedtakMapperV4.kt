package no.nav.pensjon.kalkulator.vedtak.api.map

import no.nav.pensjon.kalkulator.vedtak.*
import no.nav.pensjon.kalkulator.vedtak.api.dto.*
import java.math.BigDecimal

object LoependeVedtakMapperV4 {

    fun toDto(vedtak: LoependeVedtak) = LoependeVedtakV4(
        alderspensjon = toAlderspensjonDetaljerV4Dto(vedtak.alderspensjon),
        fremtidigAlderspensjon = toAlderspensjonDetaljerV4Dto(vedtak.fremtidigLoependeVedtakAp),
        ufoeretrygd = toUfoeretrygdDetaljerV4Dto(vedtak.ufoeretrygd),
        afpPrivat = toLoependeFraV4Dto(vedtak.afpPrivat),
        afpOffentlig = toLoependeFraV4Dto(vedtak.afpOffentlig),
    )

    private fun toAlderspensjonDetaljerV4Dto(alderspensjon: LoependeAlderspensjonDetaljer?) = alderspensjon?.let {
        AlderspensjonDetaljerV4(
            grad = alderspensjon.grad,
            fom = alderspensjon.fom,
            sisteUtbetaling = alderspensjon.utbetalingSisteMaaned?.let {
                UtbetalingV4(
                    beloep = it.beloep ?: BigDecimal(0),
                    utbetalingsdato = it.posteringsdato,
                )
            },
            sivilstand = SivilstandV4.fromInternalValue(alderspensjon.sivilstand)
        )
    }

    private fun toAlderspensjonDetaljerV4Dto(alderspensjon: FremtidigAlderspensjonDetaljer?) = alderspensjon?.let {
        FremtidigAlderspensjonDetaljerV4(
            grad = alderspensjon.grad,
            fom = alderspensjon.fom,
        )
    }


    private fun toUfoeretrygdDetaljerV4Dto(ufoeretrygd: LoependeUfoeretrygdDetaljer?) : UfoeretrygdDetaljerV4 = UfoeretrygdDetaljerV4(grad = ufoeretrygd?.grad?: 0)

    private fun toLoependeFraV4Dto(loependeVedtakDetaljer: LoependeVedtakDetaljer?) = loependeVedtakDetaljer?.fom?.let { LoependeFraV4(fom = it) }

}