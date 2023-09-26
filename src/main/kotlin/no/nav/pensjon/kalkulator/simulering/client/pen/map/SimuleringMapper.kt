package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimuleringRequestDto
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimuleringResponseDto
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

object SimuleringMapper {

    fun fromDto(dto: SimuleringResponseDto): Simuleringsresultat =
        Simuleringsresultat(
            alderspensjon = dto.alderspensjon.map { SimulertAlderspensjon(alder = it.alder, beloep = it.beloep) },
            afpPrivat = dto.afpPrivat.map { SimulertAfpPrivat(alder = it.alder, beloep = it.beloep) }
        )

    fun toDto(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ) =
        SimuleringRequestDto(
            pid = personalSpec.pid.value,
            sivilstand = PenSivilstand.fromInternalValue(personalSpec.sivilstand).externalValue,
            harEps = impersonalSpec.epsHarInntektOver2G,
            uttaksar = 1,
            sisteInntekt = personalSpec.forventetInntekt,
            forsteUttaksdato = midnight(impersonalSpec.foersteUttaksdato),
            simuleringstype = PenSimuleringstype.fromInternalValue(impersonalSpec.simuleringType).externalValue
        )

    private fun midnight(date: LocalDate) =
        Date.from(date.atTime(0, 0).toInstant(ZoneOffset.ofHours(1)))
}
