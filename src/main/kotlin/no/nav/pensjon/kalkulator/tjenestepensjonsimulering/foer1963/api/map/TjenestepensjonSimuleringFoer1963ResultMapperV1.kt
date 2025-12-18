package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.api.map

import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.Feilkode
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.OffentligTjenestepensjonSimuleringFoer1963Resultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.api.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.SimuleringsresultatStatusV2
import kotlin.math.floor

object TjenestepensjonSimuleringFoer1963ResultMapperV1 {

    fun toDtoV2(resultat: OffentligTjenestepensjonSimuleringFoer1963Resultat): OffentligTjenestepensjonSimuleringFoer1963ResultV1 {
        val periods = resultat.utbetalingsperioder.map { utbetaling ->

            UtbetalingsperiodeFoer1963V1(
                startAlder = utbetaling.alderFom,
                sluttAlder = utbetaling.alderTom,
                aarligUtbetaling = utbetaling.arligUtbetaling?.let { floor(it).toInt() } ?: 0,
                grad = utbetaling.grad,
                ytelsekode = utbetaling.ytelsekode!!,
                mangelfullSimuleringkode = utbetaling.mangelfullSimuleringkode,
                maanedligUtbetaling = utbetaling.arligUtbetaling?.let { floor(it / 12).toInt() }
            )
        }

        val simulert = if (resultat.tpnr != null && resultat.navnOrdning != null) {
            SimulertTjenestepensjonFoer1963V1(
                tpLeverandoer = resultat.navnOrdning,
                tpNummer = resultat.tpnr,
                simuleringsresultat = SimuleringsresultatFoer1963V1(utbetalingsperioder = periods)
            )
        } else null

        return OffentligTjenestepensjonSimuleringFoer1963ResultV1(
            simuleringsresultatStatus = resultat.feilkode?.let {
                when (it) {
                    Feilkode.BRUKER_IKKE_MEDLEM_AV_TP_ORDNING -> SimuleringsresultatStatusV2.BRUKER_ER_IKKE_MEDLEM_AV_TP_ORDNING
                    Feilkode.TP_ORDNING_STOETTES_IKKE -> SimuleringsresultatStatusV2.TP_ORDNING_STOETTES_IKKE
                    Feilkode.TEKNISK_FEIL -> SimuleringsresultatStatusV2.TEKNISK_FEIL
                    else -> SimuleringsresultatStatusV2.OK
                }
            } ?: SimuleringsresultatStatusV2.OK,
            muligeTpLeverandoerListe = listOfNotNull(resultat.navnOrdning),
            simulertTjenestepensjon = simulert,
            serviceData = null,
            feilkode = resultat.feilkode
        )
    }
}
