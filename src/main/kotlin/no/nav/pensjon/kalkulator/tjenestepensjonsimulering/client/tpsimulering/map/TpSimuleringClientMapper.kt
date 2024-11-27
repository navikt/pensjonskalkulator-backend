package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.map

import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.dto.SimulerTjenestepensjonResponseDto

object TpSimuleringClientMapper {

    fun mapFromDto(dto: SimulerTjenestepensjonResponseDto) = OFTPSimuleringsresultat(
        simuleringsResultatStatus = SimuleringsResultatStatus(
            resultatType = ResultatType.fromExternalValue(dto.simuleringsResultatStatus.resultatType),
            feilmelding = dto.simuleringsResultatStatus.feilmelding,
        ),
        simuleringsResultat = dto.simuleringsResultat?.let {
            SimuleringsResultat(
                tpOrdning = it.tpLeverandoer,
                perioder = it.utbetalingsperioder
                    .map { utbetalingDto ->
                        Utbetaling(
                            aar = utbetalingDto.aar,
                            beloep = utbetalingDto.beloep,
                        )
                    },
                btpInkludert = it.betingetTjenestepensjonErInkludert,
            )
        },
        tpOrdninger = dto.relevanteTpOrdninger,
    )
}