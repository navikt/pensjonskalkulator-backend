package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by the API offered to clients.
 */
object SimuleringMapper {

    fun resultatDto(resultat: Simuleringsresultat) =
        SimuleringsresultatDto(
            alderspensjon = resultat.alderspensjon.map { PensjonsberegningDto(it.alder, it.beloep) },
            afpPrivat = resultat.afpPrivat.map { PensjonsberegningDto(it.alder, it.beloep) },
            vilkaarErOppfylt = true
        )

    // V1
    fun fromSpecDto(spec: SimuleringSpecDto) =
        ImpersonalSimuleringSpec(
            simuleringType = spec.simuleringstype,
            epsHarInntektOver2G = spec.epsHarInntektOver2G,
            forventetAarligInntektFoerUttak = spec.forventetInntekt,
            sivilstand = spec.sivilstand,
            gradertUttak = null, // not supported in V1
            heltUttak = HeltUttak(
                uttakFomAlder = alder(spec.foersteUttaksalder),
                inntekt = null // not supported in V1
            )
        )

    fun fromIngressSimuleringSpecV2(spec: IngressSimuleringSpecV2) =
        ImpersonalSimuleringSpec(
            simuleringType = spec.simuleringstype,
            epsHarInntektOver2G = spec.epsHarInntektOver2G,
            forventetAarligInntektFoerUttak = spec.aarligInntektFoerUttakBeloep,
            sivilstand = spec.sivilstand,
            gradertUttak = spec.gradertUttak?.let { gradertUttak(it) },
            heltUttak = heltUttak(spec.heltUttak)
        )

    private fun alder(dto: IngressSimuleringAlderV2) = Alder(dto.aar, dto.maaneder)

    private fun gradertUttak(dto: IngressSimuleringGradertUttakV2) =
        GradertUttak(
            grad = Uttaksgrad.from(dto.grad),
            uttakFomAlder = alder(dto.uttaksalder),
            aarligInntekt = dto.aarligInntektVsaPensjonBeloep ?: 0
        )

    private fun heltUttak(dto: IngressSimuleringHeltUttakV2) =
        HeltUttak(
            uttakFomAlder = alder(dto.uttaksalder),
            inntekt = dto.aarligInntektVsaPensjon?.let(::inntekt)
        )

    private fun inntekt(dto: IngressSimuleringInntektV2) =
        Inntekt(
            aarligBeloep = dto.beloep,
            tomAlder = dto.sluttAlder.let(::alder)
        )
}
