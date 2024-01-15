package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.common.client.pen.PenUttaksgrad
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.*

object PenSimuleringMapper {

    fun fromDto(dto: SimuleringResponseDto): Simuleringsresultat =
        Simuleringsresultat(
            alderspensjon = dto.alderspensjon.map { SimulertAlderspensjon(alder = it.alder, beloep = it.beloep) },
            afpPrivat = dto.afpPrivat.map { SimulertAfpPrivat(alder = it.alder, beloep = it.beloep) }
        )

    fun toDto(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ) =
        SimuleringEgressSpecDto(
            simuleringstype = PenSimuleringType.fromInternalValue(impersonalSpec.simuleringType).externalValue,
            pid = personalSpec.pid.value,
            sivilstand = PenSivilstand.fromInternalValue(personalSpec.sivilstand).externalValue,
            harEps = impersonalSpec.epsHarInntektOver2G,
            sisteInntekt = personalSpec.forventetInntekt,
            uttaksar = 1,
            gradertUttak = impersonalSpec.gradertUttak?.let(::toGradertUttakDto),
            heltUttak = toHeltUttakDto(impersonalSpec.heltUttak)
        )

    private fun toGradertUttakDto(uttak: GradertUttak) =
        GradertUttakSpecDto(
            PenUttaksgrad.fromInternalValue(uttak.grad).externalValue,
            toAlderDto(uttak.uttakFomAlder),
            uttak.aarligInntekt
        )

    private fun toHeltUttakDto(uttak: HeltUttak) =
        HeltUttakSpecDto(
            toAlderDto(uttak.uttakFomAlder),
            uttak.inntekt?.aarligBeloep ?: 0,
            uttak.inntekt?.let { toAlderDto(it.tomAlder) } ?: toAlderDto(uttak.uttakFomAlder)
        )

    private fun toAlderDto(alder: Alder) = AlderSpecDto(alder.aar, alder.maaneder)
}
