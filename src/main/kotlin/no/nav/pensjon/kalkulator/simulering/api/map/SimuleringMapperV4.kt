package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 4 of the API offered to clients.
 */
object SimuleringMapperV4 {

    fun fromIngressSimuleringSpecV4(dto: IngressSimuleringSpecV4) =
        ImpersonalSimuleringSpec(
            simuleringType = dto.simuleringstype,
            epsHarInntektOver2G = dto.epsHarInntektOver2G,
            forventetAarligInntektFoerUttak = dto.aarligInntektFoerUttakBeloep,
            sivilstand = dto.sivilstand,
            gradertUttak = dto.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(dto.heltUttak)
        )

    fun resultatV4(source: Simuleringsresultat) =
        SimuleringResultatV4(
            alderspensjon = source.alderspensjon.map { PensjonsberegningV4(it.alder, it.beloep) },
            afpPrivat = source.afpPrivat.map { PensjonsberegningV4(it.alder, it.beloep) },
            afpOffentlig = mapAfpOffentlig(source.afpOffentlig),
            vilkaarsproeving = vilkaarsproeving(source.vilkaarsproeving)
        )

    private fun mapAfpOffentlig(afpOffentlig: List<SimulertAfpOffentlig>): AfpOffentligV4? {
        return if (afpOffentlig.isEmpty()) null
        else AfpOffentligV4(
            afpLeverandoer = afpOffentlig.first().afpLeverandoer,
            afpOffentligListe = afpOffentlig.map { PensjonsberegningAfpOffentligV4(it.alder, it.beloep) }
        )
    }

    private fun gradertUttak(dto: IngressSimuleringGradertUttakV4) =
        GradertUttak(
            grad = Uttaksgrad.from(dto.grad),
            uttakFomAlder = alder(dto.uttaksalder),
            aarligInntekt = dto.aarligInntektVsaPensjonBeloep ?: 0
        )

    private fun heltUttak(dto: IngressSimuleringHeltUttakV4) =
        HeltUttak(
            uttakFomAlder = alder(dto.uttaksalder),
            inntekt = dto.aarligInntektVsaPensjon?.let(::inntekt)
        )

    private fun inntekt(dto: IngressSimuleringInntektV4) =
        Inntekt(
            aarligBeloep = dto.beloep,
            tomAlder = dto.sluttAlder.let(::alder)
        )

    private fun alder(dto: IngressSimuleringAlderV4) = Alder(dto.aar, dto.maaneder)

    private fun vilkaarsproeving(source: Vilkaarsproeving) =
        VilkaarsproevingV4(
            vilkaarErOppfylt = source.innvilget,
            alternativ = source.alternativ?.let(::alternativ)
        )

    private fun alternativ(source: Alternativ) =
        AlternativV4(
            gradertUttaksalder = source.gradertUttakAlder?.let(::alder),
            uttaksgrad = prosentsats(source.uttakGrad),
            heltUttaksalder = alder(source.heltUttakAlder)
        )

    private fun prosentsats(grad: Uttaksgrad?): Int? =
        grad?.let {
            if (it == Uttaksgrad.HUNDRE_PROSENT) null else it.prosentsats
        }

    private fun alder(source: Alder) = AlderV4(source.aar, source.maaneder)
}
