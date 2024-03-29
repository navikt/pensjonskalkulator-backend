package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.simulering.Alternativ
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.Vilkaarsproeving
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 3 of the API offered to clients.
 */
object SimuleringMapperV3 {

    fun fromIngressSimuleringSpecV3(dto: IngressSimuleringSpecV3) =
        ImpersonalSimuleringSpec(
            simuleringType = dto.simuleringstype,
            epsHarInntektOver2G = dto.epsHarInntektOver2G,
            forventetAarligInntektFoerUttak = dto.aarligInntektFoerUttakBeloep,
            sivilstand = dto.sivilstand,
            gradertUttak = dto.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(dto.heltUttak)
        )

    fun resultatV3(source: Simuleringsresultat) =
        SimuleringResultatV3(
            alderspensjon = source.alderspensjon.map { PensjonsberegningV3(it.alder, it.beloep) },
            afpPrivat = source.afpPrivat.map { PensjonsberegningV3(it.alder, it.beloep) },
            vilkaarsproeving = vilkaarsproeving(source.vilkaarsproeving)
        )

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

    private fun alder(dto: IngressSimuleringAlderV3) = Alder(dto.aar, dto.maaneder)

    private fun vilkaarsproeving(source: Vilkaarsproeving) =
        VilkaarsproevingV3(
            vilkaarErOppfylt = source.innvilget,
            alternativ = source.alternativ?.let(::alternativ)
        )

    private fun alternativ(source: Alternativ) =
        AlternativV3(
            gradertUttaksalder = source.gradertUttakAlder?.let(::alder),
            uttaksgrad = prosentsats(source.uttakGrad),
            heltUttaksalder = alder(source.heltUttakAlder)
        )

    private fun prosentsats(grad: Uttaksgrad?): Int? =
        grad?.let {
            if (it == Uttaksgrad.HUNDRE_PROSENT) null else it.prosentsats
        }

    private fun alder(source: Alder) = AlderV3(source.aar, source.maaneder)
}
