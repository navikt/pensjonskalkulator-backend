package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.tech.time.DateUtil.MAANEDER_PER_AAR
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.OffentligTjenestepensjonSimuleringsresultat

object TjenestepensjonSimuleringResultMapperV2 {

    fun toDto(simuleringsresultat: OffentligTjenestepensjonSimuleringsresultat) = OffentligTjenestepensjonSimuleringsresultatDtoV2(
        simuleringsresultatStatus = SimuleringsresultatStatusV2.fromResultatType(simuleringsresultat.simuleringsResultatStatus.resultatType),
        muligeTpLeverandoerListe = simuleringsresultat.tpOrdninger,
        simulertTjenestepensjon = simuleringsresultat.simuleringsResultat?.let {
            SimulertTjenestepensjonV2(
                tpLeverandoer = it.tpOrdning,
                simuleringsresultat = SimuleringsresultatV2(
                    utbetalingsperioder = it.perioder
                        .map { utbetaling -> UtbetalingsperiodeV2(utbetaling.startAlder, utbetaling.sluttAlder, utbetaling.maanedligBeloep * MAANEDER_PER_AAR) },
                    betingetTjenestepensjonErInkludert = it.betingetTjenestepensjonInkludert,
                )
            )
        },
    )
}