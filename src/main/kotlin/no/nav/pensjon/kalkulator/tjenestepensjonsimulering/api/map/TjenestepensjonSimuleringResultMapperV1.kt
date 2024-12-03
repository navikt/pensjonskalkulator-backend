package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.OffentligTjenestepensjonSimuleringsresultat

object TjenestepensjonSimuleringResultMapperV1 {

    fun toDto(simuleringsresultat: OffentligTjenestepensjonSimuleringsresultat) = OffentligTjenestepensjonSimuleringsresultatDtoV1(
        simuleringsresultatStatus = SimuleringsresultatStatusV1.fromResultatType(simuleringsresultat.simuleringsResultatStatus.resultatType),
        muligeTpLeverandoerListe = simuleringsresultat.tpOrdninger,
        simulertTjenestepensjon = simuleringsresultat.simuleringsResultat?.let {
            SimulertTjenestepensjonV1(
                tpLeverandoer = it.tpOrdning,
                simuleringsresultat = SimuleringsresultatV1(
                    utbetalingsperioder = it.perioder
                        .map { utbetaling -> UtbetalingsperiodeV1(utbetaling.startAlder, utbetaling.sluttAlder, utbetaling.maanedligBeloep) },
                    betingetTjenestepensjonErInkludert = it.betingetTjenestepensjonInkludert,
                )
            )
        },
    )

}