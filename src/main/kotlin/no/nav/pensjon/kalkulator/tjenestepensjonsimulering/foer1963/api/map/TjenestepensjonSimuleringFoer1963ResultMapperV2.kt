package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.map

import no.nav.pensjon.kalkulator.general.Alder.Companion.from
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.OffentligTjenestepensjonSimuleringFoer1963Resultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.*
import java.time.LocalDate
import kotlin.math.floor

object TjenestepensjonSimuleringFoer1963ResultMapperV2 {

    fun toDtoV2(resultat: OffentligTjenestepensjonSimuleringFoer1963Resultat): OffentligTjenestepensjonSimuleringFoer1963ResultV2 {
        val periods = resultat.utbetalingsperioder.map { utbetaling ->

            UtbetalingsperiodeFoer1963V2(
                startAlder = utbetaling.alderFom,
                sluttAlder = utbetaling.alderTom,
                aarligUtbetaling = utbetaling.arligUtbetaling?.let { floor(it).toInt() } ?: 0,
                grad = utbetaling.grad,
                ytelsekode = utbetaling.ytelsekode!!,
                mangelfullSimuleringkode = utbetaling.mangelfullSimuleringkode,
                maanedligUtbetaling = 0
            )
        }

        val simulert = if (resultat.tpnr != null && resultat.navnOrdning != null) {
            SimulertTjenestepensjonFoer1963V2(
                tpLeverandoer = resultat.navnOrdning,
                tpNummer = resultat.tpnr,
                simuleringsresultat = SimuleringsresultatFoer1963V2(utbetalingsperioder = periods)
            )
        } else null

        return OffentligTjenestepensjonSimuleringFoer1963ResultV2(
            simuleringsresultatStatus = SimuleringsresultatStatusV2.OK,
            muligeTpLeverandoerListe = emptyList(),
            simulertTjenestepensjon = simulert,
            serviceData = null
        )
    }
}
