package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.Alternativ
import no.nav.pensjon.kalkulator.simulering.SimuleringResult
import no.nav.pensjon.kalkulator.simulering.Vilkaarsproeving
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 6 of the API offered to clients.
 */
object SimuleringResultMapperV6 {

    fun resultatV6(source: SimuleringResult) =
        SimuleringResultatV6(
            alderspensjon = source.alderspensjon.map { PensjonsberegningV6(it.alder, it.beloep) },
            afpPrivat = source.afpPrivat.map { PensjonsberegningV6(it.alder, it.beloep) },
            afpOffentlig = source.afpOffentlig.map { PensjonsberegningAfpOffentligV6(it.alder, it.beloep) },
            vilkaarsproeving = vilkaarsproeving(source.vilkaarsproeving),
            harForLiteTrygdetid = source.harForLiteTrygdetid
        )

    private fun vilkaarsproeving(source: Vilkaarsproeving) =
        VilkaarsproevingV6(
            vilkaarErOppfylt = source.innvilget,
            alternativ = source.alternativ?.let(::alternativ)
        )

    private fun alternativ(source: Alternativ) =
        AlternativV6(
            gradertUttaksalder = source.gradertUttakAlder?.let(::alder),
            uttaksgrad = prosentsats(source.uttakGrad),
            heltUttaksalder = alder(source.heltUttakAlder)
        )

    private fun prosentsats(grad: Uttaksgrad?): Int? =
        grad?.let {
            if (it == Uttaksgrad.HUNDRE_PROSENT) null else it.prosentsats
        }

    private fun alder(source: Alder) = AlderV6(source.aar, source.maaneder)
}
