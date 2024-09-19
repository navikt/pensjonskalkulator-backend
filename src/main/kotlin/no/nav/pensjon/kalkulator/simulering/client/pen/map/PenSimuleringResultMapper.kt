package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.common.client.pen.PenUttaksgrad
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.*

object PenSimuleringResultMapper {

    fun fromDto(dto: PenSimuleringResultDto) =
        SimuleringResult(
            alderspensjon = dto.alderspensjon.map(::alderspensjon),
            afpPrivat = dto.afpPrivat.map(::afpPrivat),
            afpOffentlig = dto.afpOffentliglivsvarig.map(::afpOffentlig),
            vilkaarsproeving = dto.vilkaarsproeving?.let(::vilkaarsproeving) ?: Vilkaarsproeving(innvilget = true),
            harForLiteTrygdetid = dto.harNokTrygdetidForGarantipensjon == false,
            opptjeningGrunnlagListe = dto.opptjeningGrunnlagListe.orEmpty().map(::opptjeningGrunnlag)
        )

    private fun opptjeningGrunnlag(dto: PenOpptjeningGrunnlag) =
        SimulertOpptjeningGrunnlag(dto.aar, dto.pensjonsgivendeInntekt)

    private fun alderspensjon(dto: PenPensjonDto) =
        SimulertAlderspensjon(
            alder = dto.alder,
            beloep = dto.beloep,
            inntektspensjonBeloep = dto.inntektspensjon ?: 0,
            garantipensjonBeloep = dto.garantipensjon ?: 0,
            delingstall = dto.delingstall ?: 0.0,
            pensjonBeholdningFoerUttak = dto.pensjonBeholdningFoerUttak ?: 0
        )

    private fun afpPrivat(dto: PenPensjonDto) = SimulertAfpPrivat(dto.alder, dto.beloep)

    private fun afpOffentlig(dto: PenPensjonAfpOffentligDto) = SimulertAfpOffentlig(dto.alder, dto.beloep)

    private fun vilkaarsproeving(dto: PenVilkaarsproevingDto) =
        Vilkaarsproeving(
            innvilget = dto.vilkaarErOppfylt,
            alternativ = dto.alternativ?.let(::alternativ)
        )

    private fun alternativ(dto: PenAlternativDto) =
        Alternativ(
            gradertUttakAlder = dto.gradertUttaksalder?.let(::alder),
            uttakGrad = dto.uttaksgrad?.let(Uttaksgrad::from),
            heltUttakAlder = alder(dto.heltUttaksalder)
        )

    private fun alder(dto: PenAlderDto) = Alder(dto.aar, dto.maaneder)
}
