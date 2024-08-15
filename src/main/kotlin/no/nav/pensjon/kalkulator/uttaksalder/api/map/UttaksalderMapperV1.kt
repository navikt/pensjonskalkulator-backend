package no.nav.pensjon.kalkulator.uttaksalder.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.simulering.UtenlandsperiodeSpec
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.*
import java.time.LocalDate

object UttaksalderMapperV1 {

    private val defaultTomAlder = Alder(99, 11)

    fun toDto(uttaksalder: Alder?): AlderDto? = uttaksalder?.let(::alderDto)

    fun fromIngressSpecForHeltUttakV1(spec: IngressUttaksalderSpecForHeltUttakV1) =
        ImpersonalUttaksalderSpec(
            sivilstand = spec.sivilstand,
            harEps = spec.harEps,
            aarligInntektFoerUttak = spec.aarligInntektFoerUttakBeloep,
            simuleringType = spec.simuleringstype ?: SimuleringType.ALDERSPENSJON,
            gradertUttak = null,
            heltUttak = spec.aarligInntektVsaPensjon?.let(::heltUttakV1),
            utenlandsperiodeListe = spec.utenlandsperiodeListe.orEmpty().map(::utenlandsperiodeSpecV1)
        )

    fun fromIngressSpecForGradertUttakV1(spec: IngressUttaksalderSpecForGradertUttakV1) =
        ImpersonalUttaksalderSpec(
            sivilstand = spec.sivilstand,
            harEps = spec.harEps,
            aarligInntektFoerUttak = spec.aarligInntektFoerUttakBeloep,
            simuleringType = spec.simuleringstype ?: SimuleringType.ALDERSPENSJON,
            gradertUttak = gradertUttakV1(spec.gradertUttak),
            heltUttak = heltUttakInGradertContextV1(spec.heltUttak),
            utenlandsperiodeListe = spec.utenlandsperiodeListe.orEmpty().map(::utenlandsperiodeSpecV1)
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

    private fun utenlandsperiodeSpecV1(dto: UttaksalderUtenlandsperiodeSpecV1) =
        UtenlandsperiodeSpec(
            fom = dto.fom,
            tom = dto.tom,
            land = Land.valueOf(dto.landkode),
            arbeidetUtenlands = dto.arbeidetUtenlands
        )

    private fun alder(dto: IngressUttaksalderAlderV1) = Alder(dto.aar, dto.maaneder)

    private fun alderDto(alder: Alder) = AlderDto(alder.aar, alder.maaneder)
}
