package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 6 of the API offered to clients.
 */
object SimuleringResultMapperV6 {

    fun resultatV6(source: SimuleringResult) =
        SimuleringResultatV6(
            alderspensjon = source.alderspensjon.map(::alderspensjon),
            afpPrivat = source.afpPrivat.map(::privatAfp),
            afpOffentlig = source.afpOffentlig.map(::offentligAfp),
            vilkaarsproeving = vilkaarsproeving(source.vilkaarsproeving),
            harForLiteTrygdetid = source.harForLiteTrygdetid,
            trygdetid = source.trygdetid,
            opptjeningGrunnlagListe = source.opptjeningGrunnlagListe.map(::opptjeningGrunnlag)
        )

    private fun alderspensjon(source: SimulertAlderspensjon) =
        AlderspensjonsberegningV6(
            source.alder,
            source.beloep,
            source.inntektspensjonBeloep,
            source.garantipensjonBeloep,
            source.delingstall,
            source.pensjonBeholdningFoerUttak
        )

    private fun offentligAfp(source: SimulertAfpOffentlig) =
        PensjonsberegningAfpOffentligV6(source.alder, source.beloep)

    private fun privatAfp(source: SimulertAfpPrivat) =
        PensjonsberegningV6(source.alder, source.beloep)

    private fun opptjeningGrunnlag(source: SimulertOpptjeningGrunnlag) =
        SimulertOpptjeningGrunnlagV6(
            source.aar,
            source.pensjonsgivendeInntektBeloep
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
