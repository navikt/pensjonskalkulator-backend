package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.map

import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.OffentligTjenestepensjonSimuleringFoer1963Resultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.*

object TjenestepensjonSimuleringFoer1963ResultMapperV2 {

    fun toDtoV2(resultat: OffentligTjenestepensjonSimuleringFoer1963Resultat): OffentligTjenestepensjonSimuleringFoer1963ResultV2 {
        val periods = resultat.utbetalingsperioder?.map { utbetaling ->
            UtbetalingsperiodeFoer1963V2(
                datoFom = utbetaling.datoFom,
                datoTom = utbetaling.datoTom,
                grad = utbetaling.grad,
                arligUtbetaling = utbetaling.arligUtbetaling,
                ytelsekode = utbetaling.ytelsekode,
                mangelfullSimuleringkode = utbetaling.mangelfullSimuleringkode
            )
        } ?: emptyList()

        val simulert = if (resultat.tpnr != null || resultat.navnOrdning != null || periods.isNotEmpty()) {
            SimulertTjenestepensjonFoer1963V2(
                tpLeverandoer = resultat.navnOrdning,
                tpNummer = resultat.tpnr,
                simuleringsresultat = SimuleringsresultatFoer1963V2(utbetalingsperioder = periods)
            )
        } else null

        return OffentligTjenestepensjonSimuleringFoer1963ResultV2(
            simuleringsresultatStatus = SimuleringsresultatStatusV2.OK, // No status provided in domain model yet
            muligeTpLeverandoerListe = emptyList(),
            simulertTjenestepensjon = simulert,
            serviceData = null
        )
    }
}
