package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.common.client.pen.PenUttaksgrad
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.*

object PenSimuleringMapper {

    fun fromDto(dto: PenSimuleringResultDto) =
        Simuleringsresultat(
            alderspensjon = dto.alderspensjon.map(::alderspensjon),
            afpPrivat = dto.afpPrivat.map(::afpPrivat),
            afpOffentlig = dto.afpOffentliglivsvarig.map (::afpOffentlig),
            vilkaarsproeving = dto.vilkaarsproeving?.let(::vilkaarsproeving) ?: Vilkaarsproeving(innvilget = true)
        )

    fun toDto(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ) =
        SimuleringEgressSpecDto(
            simuleringstype = PenSimuleringType.fromInternalValue(impersonalSpec.simuleringType).externalValue,
            pid = personalSpec.pid.value,
            sivilstand = PenSivilstand.fromInternalValue(personalSpec.sivilstand).externalValue,
            epsHarInntektOver2G = impersonalSpec.epsHarInntektOver2G,
            epsHarPensjon = false, // NB: Ikke-støttet verdi
            sisteInntekt = personalSpec.aarligInntektFoerUttak,
            uttaksar = 1,
            gradertUttak = impersonalSpec.gradertUttak?.let(::gradertUttakSpecDto),
            heltUttak = heltUttakSpecDto(impersonalSpec.heltUttak)
        )

    private fun alderspensjon(dto: PenPensjonDto) = SimulertAlderspensjon(dto.alder, dto.beloep)

    private fun afpPrivat(dto: PenPensjonDto) = SimulertAfpPrivat(dto.alder, dto.beloep)

    private fun afpOffentlig(dto: PenPensjonAfpOffentligDto) = SimulertAfpOffentlig(dto.alder, dto.beloep, dto.tpOrdning)

    private fun vilkaarsproeving(dto: PenVilkaarsproevingDto) =
        Vilkaarsproeving(
            innvilget = dto.vilkaarErOppfylt,
            alternativ = dto.alternativ?.let(::alternativ)
        )

    private fun alternativ(dto: PenAlternativDto) =
        Alternativ(
            gradertUttakAlder = dto.gradertUttaksalder?.let(::alder),
            uttakGrad = dto.uttaksgrad?.let(Uttaksgrad::from),
            heltUttakAlder = alder(dto.heltUttaksalder)
        )

    private fun alder(dto: PenAlderDto) = Alder(dto.aar, dto.maaneder)

    private fun gradertUttakSpecDto(uttak: GradertUttak) =
        GradertUttakSpecDto(
            grad = PenUttaksgrad.fromInternalValue(uttak.grad).externalValue,
            uttakFomAlder = alderSpecDto(uttak.uttakFomAlder),
            aarligInntekt = uttak.aarligInntekt
        )

    private fun heltUttakSpecDto(uttak: HeltUttak) =
        HeltUttakSpecDto(
            uttakFomAlder = alderSpecDto(uttak.uttakFomAlder!!), // mandatory in context of simulering
            aarligInntekt = uttak.inntekt?.aarligBeloep ?: 0,
            inntektTomAlder = uttak.inntekt?.let { alderSpecDto(it.tomAlder) } ?: alderSpecDto(uttak.uttakFomAlder)
        )

    private fun alderSpecDto(alder: Alder) = AlderSpecDto(alder.aar, alder.maaneder)
}
