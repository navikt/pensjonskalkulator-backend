package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.tech.time.DateUtil.MAANEDER_PER_AAR
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.OffentligTjenestepensjonSimuleringsresultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.SimuleringsResultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.Utbetaling

object TjenestepensjonSimuleringResultMapperV2 {

    fun toDtoV2(resultat: OffentligTjenestepensjonSimuleringsresultat) =
        OffentligTjenestepensjonSimuleringResultV2(
            simuleringsresultatStatus = SimuleringsresultatStatusV2.fromResultatType(resultat.simuleringsResultatStatus.resultatType),
            muligeTpLeverandoerListe = resultat.tpOrdninger,
            simulertTjenestepensjon = resultat.simuleringsResultat?.let(::simulertTjenestepensjon),
            serviceData = resultat.serviceData
        )

    private fun simulertTjenestepensjon(resultat: SimuleringsResultat) =
        SimulertTjenestepensjonV2(
            tpLeverandoer = resultat.tpOrdning,
            tpNummer = resultat.tpNummer,
            simuleringsresultat = SimuleringsresultatV2(
                utbetalingsperioder = resultat.perioder.map(::utbetalingsperiode),
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
