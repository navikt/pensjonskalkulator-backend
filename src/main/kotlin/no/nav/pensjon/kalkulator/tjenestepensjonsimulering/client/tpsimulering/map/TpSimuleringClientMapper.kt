package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.map

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.dto.FremtidigInntektSimuleringOFTPSpecDto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.dto.SimulerTjenestepensjonResponseDto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.dto.SimuleringOFTPSpecDto

object TpSimuleringClientMapper {

    fun fromDto(dto: SimulerTjenestepensjonResponseDto) = OffentligTjenestepensjonSimuleringsresultat(
        simuleringsResultatStatus = SimuleringsResultatStatus(
            resultatType = ResultatType.fromExternalValue(dto.simuleringsResultatStatus.resultatType),
            feilmelding = dto.simuleringsResultatStatus.feilmelding,
        ),
        simuleringsResultat = dto.simuleringsResultat?.let {
            SimuleringsResultat(
                tpOrdning = it.tpLeverandoer,
                tpNummer = it.tpNummer,
                perioder = it.utbetalingsperioder
                    .map { utbetalingDto ->
                        Utbetaling(
                            startAlder = utbetalingDto.startAlder,
                            sluttAlder = utbetalingDto.sluttAlder,
                            maanedligBeloep = utbetalingDto.maanedligBeloep
                        )
                    },
                betingetTjenestepensjonInkludert = it.betingetTjenestepensjonErInkludert,
            )
        },
        tpOrdninger = dto.relevanteTpOrdninger,
        serviceData = dto.serviceData.orEmpty(),
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

    fun toDto(spec: SimuleringOffentligTjenestepensjonSpecV2, pid: Pid) = SimuleringOFTPSpecDto(
        pid = pid.value,
        foedselsdato = spec.foedselsdato,
        uttaksdato = spec.uttaksdato,
        sisteInntekt = spec.sisteInntekt,
        fremtidigeInntekter = spec.fremtidigeInntekter.map {
            FremtidigInntektSimuleringOFTPSpecDto(
                fraOgMed = it.fom,
                aarligInntekt = it.beloep,
            )
        },
        aarIUtlandetEtter16 = spec.aarIUtlandetEtter16,
        brukerBaOmAfp = spec.brukerBaOmAfp,
        epsPensjon = spec.epsPensjon,
        eps2G = spec.eps2G,
    )
}