package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 3 of the API offered to clients.
 */
object SimuleringMapperV3 {

    fun fromIngressSimuleringSpecV3(spec: IngressSimuleringSpecV3) =
        ImpersonalSimuleringSpec(
            simuleringType = spec.simuleringstype,
            epsHarInntektOver2G = spec.epsHarInntektOver2G,
            forventetAarligInntektFoerUttak = spec.aarligInntektFoerUttakBeloep,
            sivilstand = spec.sivilstand,
            gradertUttak = spec.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(spec.heltUttak)
        )

    fun resultatV3(resultat: Simuleringsresultat) =
        SimuleringResultatV3(
            alderspensjon = resultat.alderspensjon.map { PensjonsberegningV3(it.alder, it.beloep) },
            afpPrivat = resultat.afpPrivat.map { PensjonsberegningV3(it.alder, it.beloep) },
            vilkaarsproeving = VilkaarsproevingV3(vilkaarErOppfylt = true, alternativ = null)
        )


    private fun alder(dto: IngressSimuleringAlderV3) = Alder(dto.aar, dto.maaneder)

    private fun gradertUttak(dto: IngressSimuleringGradertUttakV3) =
        GradertUttak(
            grad = Uttaksgrad.from(dto.grad),
            uttakFomAlder = alder(dto.uttaksalder),
            aarligInntekt = dto.aarligInntektVsaPensjonBeloep ?: 0
        )

    private fun heltUttak(dto: IngressSimuleringHeltUttakV3) =
        HeltUttak(
            uttakFomAlder = alder(dto.uttaksalder),
            inntekt = dto.aarligInntektVsaPensjon?.let(::inntekt)
        )

    private fun inntekt(dto: IngressSimuleringInntektV3) =
        Inntekt(
            aarligBeloep = dto.beloep,
            tomAlder = dto.sluttAlder.let(::alder)
        )
}
