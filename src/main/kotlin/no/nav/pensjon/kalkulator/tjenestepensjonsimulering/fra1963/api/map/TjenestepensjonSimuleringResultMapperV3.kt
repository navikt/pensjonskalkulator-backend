package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.map

import no.nav.pensjon.kalkulator.tech.time.DateUtil.MAANEDER_PER_AAR
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.OffentligTjenestepensjonSimuleringsresultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.SimuleringsResultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.TpOrdning
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.Utbetaling
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.*

object TjenestepensjonSimuleringResultMapperV3 {

    fun toDtoV3(resultat: OffentligTjenestepensjonSimuleringsresultat) =
        OffentligTjenestepensjonSimuleringResultV3(
            simuleringsresultatStatus = SimuleringsresultatStatusV3.fromResultatType(resultat.simuleringsResultatStatus.resultatType),
            muligeTpLeverandoerListe = resultat.tpOrdninger.map { tpOrdning(it) },
            simulertTjenestepensjon = resultat.simuleringsResultat?.let(TjenestepensjonSimuleringResultMapperV3::simulertTjenestepensjon),
            serviceData = resultat.serviceData
        )

    private fun simulertTjenestepensjon(resultat: SimuleringsResultat) =
        SimulertTjenestepensjonV3(
            tpLeverandoer = resultat.tpLeverandoerNavn,
            tpOrdning = tpOrdning(resultat.tpOrdning),
            simuleringsresultat = SimuleringsresultatV3(
                utbetalingsperioder = resultat.perioder.map(TjenestepensjonSimuleringResultMapperV3::utbetalingsperiode),
                betingetTjenestepensjonErInkludert = resultat.betingetTjenestepensjonInkludert,
            )
        )

    private fun utbetalingsperiode(utbetaling: Utbetaling) =
        UtbetalingsperiodeV3(
            startAlder = utbetaling.startAlder,
            sluttAlder = utbetaling.sluttAlder,
            aarligUtbetaling = utbetaling.maanedligBeloep * MAANEDER_PER_AAR,
            maanedligUtbetaling = utbetaling.maanedligBeloep
        )

    private fun tpOrdning(tpOrdning: TpOrdning) =
        TpOrdningV3(
            tpNummer = tpOrdning.tpNummer,
            tpNavn = tpOrdning.tpOrdningNavn
        )
}
