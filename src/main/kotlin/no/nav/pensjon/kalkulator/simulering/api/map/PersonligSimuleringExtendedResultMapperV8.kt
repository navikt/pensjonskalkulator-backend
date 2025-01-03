package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 6 of the API offered to clients.
 */
object PersonligSimuleringExtendedResultMapperV8 {

    fun extendedResultV8(source: SimuleringResult) =
        PersonligSimuleringResultV8(
            alderspensjon = source.alderspensjon.map(::alderspensjon),
            alderspensjonMaanedligVedEndring = maanedligPensjon(source.alderspensjonMaanedsbeloep),
            afpPrivat = source.afpPrivat.map(::privatAfp),
            afpOffentlig = source.afpOffentlig.map(::offentligAfp),
            vilkaarsproeving = vilkaarsproeving(source.vilkaarsproeving),
            harForLiteTrygdetid = source.harForLiteTrygdetid,
            trygdetid = source.trygdetid,
            opptjeningGrunnlagListe = source.opptjeningGrunnlagListe.map(::opptjeningGrunnlag)
        )

    private fun alderspensjon(source: SimulertAlderspensjon) =
        PersonligSimuleringAlderspensjonResultV8(
            alder = source.alder,
            beloep = source.beloep,
            inntektspensjonBeloep = source.inntektspensjonBeloep,
            garantipensjonBeloep = source.garantipensjonBeloep,
            delingstall = source.delingstall,
            pensjonBeholdningFoerUttakBeloep = source.pensjonBeholdningFoerUttak
        )

    private fun maanedligPensjon(source: AlderspensjonMaanedsbeloep?) =
        PersonligSimuleringMaanedligPensjonResultV8(
            gradertUttakMaanedligBeloep = source?.gradertUttak,
            heltUttakMaanedligBeloep = source?.heltUttak ?: 0
        )

    private fun privatAfp(source: SimulertAfpPrivat) =
        PersonligSimuleringAarligPensjonResultV8(alder = source.alder, beloep = source.beloep)

    private fun offentligAfp(source: SimulertAfpOffentlig) =
        PersonligSimuleringAarligPensjonResultV8(alder = source.alder, beloep = source.beloep)

    private fun vilkaarsproeving(source: Vilkaarsproeving) =
        PersonligSimuleringVilkaarsproevingResultV8(
            vilkaarErOppfylt = source.innvilget,
            alternativ = source.alternativ?.let(::alternativ)
        )

    private fun opptjeningGrunnlag(source: SimulertOpptjeningGrunnlag) =
        PersonligSimuleringAarligInntektResultV8(
            aar = source.aar,
            pensjonsgivendeInntektBeloep = source.pensjonsgivendeInntektBeloep
        )

    private fun alternativ(source: Alternativ) =
        PersonligSimuleringAlternativResultV8(
            gradertUttaksalder = source.gradertUttakAlder?.let(::alder),
            uttaksgrad = prosentsats(source.uttakGrad),
            heltUttaksalder = alder(source.heltUttakAlder)
        )

    private fun prosentsats(grad: Uttaksgrad?): Int? =
        grad?.let {
            if (it == Uttaksgrad.HUNDRE_PROSENT) null else it.prosentsats
        }

    private fun alder(source: Alder) =
        PersonligSimuleringAlderResultV8(aar = source.aar, maaneder = source.maaneder)
}
