package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 5 of the API offered to clients.
 */
object SimuleringMapperV5 {

    fun fromIngressSimuleringSpecV5(dto: IngressSimuleringSpecV5) =
        ImpersonalSimuleringSpec(
            simuleringType = dto.simuleringstype,
            epsHarInntektOver2G = dto.epsHarInntektOver2G,
            forventetAarligInntektFoerUttak = dto.aarligInntektFoerUttakBeloep,
            sivilstand = dto.sivilstand,
            gradertUttak = dto.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(dto.heltUttak)
        )

    fun resultatV5(source: Simuleringsresultat) =
        SimuleringResultatV5(
            alderspensjon = source.alderspensjon.map { PensjonsberegningV5(it.alder, it.beloep) },
            afpPrivat = if (source.afpPrivat.isEmpty()) null else AfpPrivatV5(source.afpPrivat.map { PensjonsberegningV5(it.alder, it.beloep) }),
            afpOffentlig = mapAfpOffentlig(source.afpOffentlig),
            vilkaarsproeving = vilkaarsproeving(source.vilkaarsproeving)
        )

    private fun mapAfpOffentlig(afpOffentlig: List<SimulertAfpOffentlig>) = if (afpOffentlig.isEmpty()) null
        else AfpOffentligV5(
            afpLeverandoer = afpOffentlig.first().afpLeverandoer,
            afpOffentligListe = afpOffentlig.map { PensjonsberegningAfpOffentligV5(it.alder, it.beloep) }
        )

    private fun gradertUttak(dto: IngressSimuleringGradertUttakV5) =
        GradertUttak(
            grad = Uttaksgrad.from(dto.grad),
            uttakFomAlder = alder(dto.uttaksalder),
            aarligInntekt = dto.aarligInntektVsaPensjonBeloep ?: 0
        )

    private fun heltUttak(dto: IngressSimuleringHeltUttakV5) =
        HeltUttak(
            uttakFomAlder = alder(dto.uttaksalder),
            inntekt = dto.aarligInntektVsaPensjon?.let(::inntekt)
        )

    private fun inntekt(dto: IngressSimuleringInntektV5) =
        Inntekt(
            aarligBeloep = dto.beloep,
            tomAlder = dto.sluttAlder.let(::alder)
        )

    private fun alder(dto: IngressSimuleringAlderV5) = Alder(dto.aar, dto.maaneder)

    private fun vilkaarsproeving(source: Vilkaarsproeving) =
        VilkaarsproevingV5(
            vilkaarErOppfylt = source.innvilget,
            alternativ = source.alternativ?.let(::alternativ)
        )

    private fun alternativ(source: Alternativ) =
        AlternativV5(
            gradertUttaksalder = source.gradertUttakAlder?.let(::alder),
            uttaksgrad = prosentsats(source.uttakGrad),
            heltUttaksalder = alder(source.heltUttakAlder)
        )

    private fun prosentsats(grad: Uttaksgrad?): Int? =
        grad?.let {
            if (it == Uttaksgrad.HUNDRE_PROSENT) null else it.prosentsats
        }

    private fun alder(source: Alder) = AlderV5(source.aar, source.maaneder)
}
