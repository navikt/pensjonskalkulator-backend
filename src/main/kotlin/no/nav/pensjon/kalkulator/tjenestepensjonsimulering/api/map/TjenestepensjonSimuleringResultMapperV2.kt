package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.OffentligTjenestepensjonSimuleringsresultat

object TjenestepensjonSimuleringResultMapperV2 {

    fun toDto(simuleringsresultat: OffentligTjenestepensjonSimuleringsresultat) = OffentligTjenestepensjonSimuleringsresultatDto(
        simuleringsresultatStatus = SimuleringsresultatStatus.fromResultatType(simuleringsresultat.simuleringsResultatStatus.resultatType),
        muligeTpLeverandoerListe = simuleringsresultat.tpOrdninger,
        simulertTjenestepensjon = simuleringsresultat.simuleringsResultat?.let {
            SimulertTjenestepensjon(
                tpLeverandoer = it.tpOrdning,
                simuleringsresultat = Simuleringsresultat(
                    utbetalingsperioder = it.perioder
                        .map { utbetaling -> UtbetalingPerAar(utbetaling.aar, utbetaling.beloep) },
                    betingetTjenestepensjonErInkludert = it.betingetTjenestepensjonInkludert,
                )
            )
        },
    )

}