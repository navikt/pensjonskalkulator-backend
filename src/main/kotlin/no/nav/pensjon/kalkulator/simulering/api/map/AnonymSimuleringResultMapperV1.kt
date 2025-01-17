package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 1 of the API offered to clients.
 */
object AnonymSimuleringResultMapperV1 {

    fun resultatV1(source: SimuleringResult) =
        AnonymSimuleringResultV1(
            alderspensjon = source.alderspensjon.map { AnonymPensjonsberegningV1(it.alder, it.beloep) },
            afpPrivat = source.afpPrivat.map { AnonymPensjonsberegningV1(it.alder, it.beloep) },
            afpOffentlig = source.afpOffentlig.map { AnonymPensjonsberegningAfpOffentligV1(it.alder, it.beloep) },
            vilkaarsproeving = vilkaarsproeving(source.vilkaarsproeving)
        )

    fun errorV1(source: SimuleringError) =
        AnonymSimuleringErrorV1(
            status = source.status,
            message = source.message
        )

    private fun vilkaarsproeving(source: Vilkaarsproeving) =
        AnonymVilkaarsproevingV1(
            vilkaarErOppfylt = source.innvilget,
            alternativ = source.alternativ?.let(::alternativ)
        )

    private fun alternativ(source: Alternativ) =
        AnonymAlternativV1(
            gradertUttaksalder = source.gradertUttakAlder?.let(::alder),
            uttaksgrad = prosentsats(source.uttakGrad),
            heltUttaksalder = alder(source.heltUttakAlder)
        )

    private fun prosentsats(grad: Uttaksgrad?): Int? =
        grad?.let {
            if (it == Uttaksgrad.HUNDRE_PROSENT) null else it.prosentsats
        }

    private fun alder(source: Alder) = AnonymAlderV1(source.aar, source.maaneder)
}
