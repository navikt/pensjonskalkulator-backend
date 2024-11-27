package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.OFTPSimuleringsresultat

object TjenestepensjonSimuleringMapper {

    fun toDto(simuleringsresultat: OFTPSimuleringsresultat) = OFTPSimuleringsresultatDto(
        simuleringsresultatStatus = SimuleringsresultatStatus.fromResultatType(simuleringsresultat.simuleringsResultatStatus.resultatType),
        muligeTpLeverandoerListe = simuleringsresultat.tpOrdninger,
        simulertTjenestepensjon = simuleringsresultat.simuleringsResultat?.let {
            SimulertTjenestepensjon(
                tpLeverandoer = it.tpOrdning,
                simuleringsresultat = Simuleringsresultat(
                    utbetalingsperioder = it.perioder
                        .map { utbetaling ->
                            UtbetalingPerAar(
                                aar = utbetaling.aar,
                                beloep = utbetaling.beloep,
                            )
                        },
                    betingetTjenestepensjonErInkludert = it.btpInkludert,
                )
            )
        },
    )

}