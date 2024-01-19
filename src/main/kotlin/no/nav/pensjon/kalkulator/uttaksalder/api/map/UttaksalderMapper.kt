package no.nav.pensjon.kalkulator.uttaksalder.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.*
import java.time.LocalDate

object UttaksalderMapper {

    private val defaultTomAlder = Alder(99, 11)

    fun toDto(uttaksalder: Alder?): AlderDto? = uttaksalder?.let(::alderDto)

    fun fromIngressSpecForHeltUttakV1(spec: IngressUttaksalderSpecForHeltUttakV1) =
        ImpersonalUttaksalderSpec(
            sivilstand = spec.sivilstand,
            harEps = spec.harEps,
            aarligInntektFoerUttak = spec.aarligInntektFoerUttakBeloep,
            simuleringType = spec.simuleringstype ?: SimuleringType.ALDERSPENSJON,
            gradertUttak = null,
            heltUttak = spec.aarligInntektVsaPensjon?.let(::heltUttakV1)
        )

    fun fromIngressSpecForGradertUttakV1(spec: IngressUttaksalderSpecForGradertUttakV1) =
        ImpersonalUttaksalderSpec(
            sivilstand = spec.sivilstand,
            harEps = spec.harEps,
            aarligInntektFoerUttak = spec.aarligInntektFoerUttakBeloep,
            simuleringType = spec.simuleringstype ?: SimuleringType.ALDERSPENSJON,
            gradertUttak = gradertUttakV1(spec.gradertUttak),
            heltUttak = heltUttakInGradertContextV1(spec.heltUttak)
        )

    private fun heltUttakV1(spec: IngressUttaksalderInntektV1) =
        HeltUttak(
            uttakFomAlder = null, // this is the value to be found
            inntekt = inntektV1(spec)
        )

    private fun heltUttakInGradertContextV1(spec: IngressUttaksalderHeltUttakV1) =
        HeltUttak(
            uttakFomAlder = alder(spec.uttaksalder),
            inntekt = spec.aarligInntektVsaPensjon?.let(::inntektV1)
        )

    private fun inntektV1(spec: IngressUttaksalderInntektV1) =
        Inntekt(
            aarligBeloep = spec.beloep,
            tomAlder = spec.sluttAlder?.let(::alder) ?: defaultTomAlder
        )

    private fun gradertUttakV1(dto: IngressUttaksalderGradertUttakV1) =
        UttaksalderGradertUttak(
            grad = Uttaksgrad.from(dto.grad),
            aarligInntekt = dto.aarligInntektVsaPensjonBeloep ?: 0,
            foedselDato = LocalDate.MIN // deprecated; irrelevant here
        )

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
            aarligInntekt = dto.aarligInntekt ?: 0,
            foedselDato = LocalDate.MIN // not in V2
        )

    private fun inntektV2(dto: UttaksalderInntektDtoV2): Inntekt? =
        dto.sluttAlder?.let { Inntekt(dto.beloep, alder(it)) }

    private fun alder(dto: UttaksalderAlderDto) = Alder(dto.aar, dto.maaneder)

    private fun alder(dto: IngressUttaksalderAlderV1) = Alder(dto.aar, dto.maaneder)

    private fun alderDto(alder: Alder) = AlderDto(alder.aar, alder.maaneder)
}
