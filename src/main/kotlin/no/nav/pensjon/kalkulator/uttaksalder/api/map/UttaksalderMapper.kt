package no.nav.pensjon.kalkulator.uttaksalder.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.*
import java.time.LocalDate

object UttaksalderMapper {

    fun toDto(uttaksalder: Alder?): AlderDto? = uttaksalder?.let(::alderDto)

    fun fromIngressSpecDto(spec: UttaksalderIngressSpecDto) =
        ImpersonalUttaksalderSpec(
            sivilstand = spec.sivilstand,
            harEps = spec.harEps,
            aarligInntektFoerUttak = spec.sisteInntekt,
            simuleringType = spec.simuleringstype ?: SimuleringType.ALDERSPENSJON,
            gradertUttak = spec.gradertUttak?.let(::gradertUttak),
            heltUttak = HeltUttak(spec.gradertUttak?.let { alder(it.heltUttakAlder) } ?: Alder(0, 0), null)
        )

    fun fromIngressSpecDtoV2(spec: UttaksalderIngressSpecDtoV2) =
        ImpersonalUttaksalderSpec(
            sivilstand = spec.sivilstand,
            harEps = spec.harEps,
            aarligInntektFoerUttak = spec.aarligInntekt,
            simuleringType = spec.simuleringstype ?: SimuleringType.ALDERSPENSJON,
            gradertUttak = spec.gradertUttak?.let(::gradertUttakV2),
            heltUttak = heltUttak(spec.heltUttak)
        )

    private fun gradertUttak(dto: UttaksalderGradertUttakIngressDto) =
        UttaksalderGradertUttak(
            grad = Uttaksgrad.from(dto.grad),
            aarligInntekt = dto.aarligInntektVsaPensjon ?: 0,
            foedselDato = dto.foedselsdato
        )

    private fun heltUttak(dto: UttaksalderHeltUttakIngressDtoV2) =
        HeltUttak(
            uttakFomAlder = alder(dto.uttaksalder),
            inntekt = inntektV2(dto.aarligInntektVsaPensjon)
        )

    private fun gradertUttakV2(dto: UttaksalderGradertUttakIngressDtoV2) =
        UttaksalderGradertUttak(
            grad = Uttaksgrad.from(dto.grad),
            aarligInntekt = dto.aarligInntektVsaPensjon ?: 0,
            foedselDato = LocalDate.MIN // not in V2
        )

    private fun inntektV2(dto: UttaksalderInntektDtoV2): Inntekt? =
        dto.sluttalder?.let { Inntekt(dto.beloep, alder(it)) }

    private fun alder(dto: AlderIngressDto) = Alder(dto.aar, dto.maaneder)

    private fun alderDto(alder: Alder) = AlderDto(alder.aar, alder.maaneder)
}
