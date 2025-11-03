package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.map

import no.nav.pensjon.kalkulator.tech.time.DateUtil.MAANEDER_PER_AAR
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.OffentligTjenestepensjonSimuleringFoer1963Resultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.SimuleringsResultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.Utbetaling
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.*

object TjenestepensjonSimuleringFoer1963ResultMapperV2 {

    fun toDtoV2(resultat: OffentligTjenestepensjonSimuleringFoer1963Resultat) =
        OffentligTjenestepensjonSimuleringFoer1963ResultV2(
            tpnr = resultat.tpnr,
            navnOrdning = resultat.navnOrdning,
            utbetalingsperioder = resultat.utbetalingsperioder?.map { utbetaling ->
                UtbelatingsperiodeFoer1963ResultV2(
                    datoFom = utbetaling.datoFom,
                    datoTom = utbetaling.datoTom,
                    grad = utbetaling.grad,
                    arligUtbetaling = utbetaling.arligUtbetaling,
                    ytelsekode = utbetaling.ytelsekode,
                    mangelfullSimuleringkode = utbetaling.mangelfullSimuleringkode
                )
            }
        )

    private fun simulertTjenestepensjon(resultat: SimuleringsResultat) =
        SimulertTjenestepensjonV2(
            tpLeverandoer = resultat.tpOrdning,
            tpNummer = resultat.tpNummer,
            simuleringsresultat = SimuleringsresultatV2(
                utbetalingsperioder = resultat.perioder.map(TjenestepensjonSimuleringFoer1963ResultMapperV2::utbetalingsperiode),
                betingetTjenestepensjonErInkludert = resultat.betingetTjenestepensjonInkludert,
            )
        )

    private fun utbetalingsperiode(utbetaling: Utbetaling) =
        UtbetalingsperiodeV2(
            startAlder = utbetaling.startAlder,
            sluttAlder = utbetaling.sluttAlder,
            aarligUtbetaling = utbetaling.maanedligBeloep * MAANEDER_PER_AAR,
            maanedligUtbetaling = utbetaling.maanedligBeloep
        )
}
