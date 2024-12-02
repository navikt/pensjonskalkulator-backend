package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.map

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.dto.SimulerTjenestepensjonResponseDto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.dto.SimuleringOFTPSpecDto

object TpSimuleringClientMapper {

    fun fromDto(dto: SimulerTjenestepensjonResponseDto) = OFTPSimuleringsresultat(
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
                betingetTjenestepensjonInkludert = it.betingetTjenestepensjonErInkludert,
            )
        },
        tpOrdninger = dto.relevanteTpOrdninger,
    )

    fun toDto(spec: SimuleringOffentligTjenestepensjonSpec, pid: Pid) = SimuleringOFTPSpecDto(
        pid = pid.value,
        foedselsdato = spec.foedselsdato,
        uttaksdato = spec.uttaksdato,
        sisteInntekt = spec.sisteInntekt,
        aarIUtlandetEtter16 = spec.aarIUtlandetEtter16,
        brukerBaOmAfp = spec.brukerBaOmAfp,
        epsPensjon = spec.epsPensjon,
        eps2G = spec.eps2G,
    )
}