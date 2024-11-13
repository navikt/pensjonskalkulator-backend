package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 6 of the API offered to clients.
 */
object SimuleringResultMapperV7 {

    fun resultatV7(source: SimuleringResult) =
        SimuleringResultatV7(
            alderspensjon = source.alderspensjon.map(::alderspensjon),
            alderspensjonMaanedligVedEndring = AlderspensjonsMaanedligV7(
                gradertUttakMaanedligBeloep = source.alderspensjonMaanedsbeloep?.gradertUttak,
                heltUttakMaanedligBeloep = source.alderspensjonMaanedsbeloep?.heltUttak ?: 0,
            ),
            afpPrivat = source.afpPrivat.map(::privatAfp),
            afpOffentlig = source.afpOffentlig.map(::offentligAfp),
            vilkaarsproeving = vilkaarsproeving(source.vilkaarsproeving),
            harForLiteTrygdetid = source.harForLiteTrygdetid,
        )

    private fun alderspensjon(source: SimulertAlderspensjon) =
        AlderspensjonsberegningV7(
            source.alder,
            source.beloep
        )

    private fun offentligAfp(source: SimulertAfpOffentlig) =
        PensjonsberegningAfpOffentligV7(source.alder, source.beloep)

    private fun privatAfp(source: SimulertAfpPrivat) =
        PensjonsberegningV7(source.alder, source.beloep)

    private fun vilkaarsproeving(source: Vilkaarsproeving) =
        VilkaarsproevingV7(
            vilkaarErOppfylt = source.innvilget,
            alternativ = source.alternativ?.let(::alternativ)
        )

    private fun alternativ(source: Alternativ) =
        AlternativV7(
            gradertUttaksalder = source.gradertUttakAlder?.let(::alder),
            uttaksgrad = prosentsats(source.uttakGrad),
            heltUttaksalder = alder(source.heltUttakAlder)
        )

    private fun prosentsats(grad: Uttaksgrad?): Int? =
        grad?.let {
            if (it == Uttaksgrad.HUNDRE_PROSENT) null else it.prosentsats
        }

    private fun alder(source: Alder) = AlderV7(source.aar, source.maaneder)
}
