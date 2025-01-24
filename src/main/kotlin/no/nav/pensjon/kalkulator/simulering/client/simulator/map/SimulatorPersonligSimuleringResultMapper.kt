package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.*

object SimulatorPersonligSimuleringResultMapper {

    fun fromDto(dto: SimulatorPersonligSimuleringResult) =
        SimuleringResult(
            alderspensjon = dto.alderspensjonListe.map(::alderspensjon),
            alderspensjonMaanedsbeloep = dto.alderspensjonMaanedsbeloep?.let(::alderspensjonMaanedsbeloep),
            afpPrivat = dto.privatAfpListe.map(::privatAfp),
            afpOffentlig = dto.livsvarigOffentligAfpListe.map(::livsvarigOffentligAfp),
            vilkaarsproeving = dto.vilkaarsproeving?.let(::vilkaarsproeving) ?: Vilkaarsproeving(innvilget = true),
            harForLiteTrygdetid = dto.tilstrekkeligTrygdetidForGarantipensjon == false,
            trygdetid = dto.trygdetid ?: 0,
            opptjeningGrunnlagListe = dto.opptjeningGrunnlagListe.orEmpty().map(::opptjeningGrunnlag)
        )

    private fun alderspensjon(dto: SimulatorPersonligPensjon) =
        SimulertAlderspensjon(
            alder = dto.alderAar,
            beloep = dto.beloep,
            inntektspensjonBeloep = dto.inntektspensjon ?: 0,
            garantipensjonBeloep = dto.garantipensjon ?: 0,
            delingstall = dto.delingstall ?: 0.0,
            pensjonBeholdningFoerUttak = dto.pensjonBeholdningFoerUttak ?: 0
        )

    private fun privatAfp(dto: SimulatorPersonligPrivatAfp) =
        SimulertAfpPrivat(dto.alderAar, dto.beloep)

    private fun livsvarigOffentligAfp(dto: SimulatorPersonligLivsvarigOffentligAfp) =
        SimulertAfpOffentlig(dto.alderAar, dto.beloep)

    private fun vilkaarsproeving(dto: SimulatorPersonligVilkaarsproeving) =
        Vilkaarsproeving(
            innvilget = dto.vilkaarErOppfylt,
            alternativ = dto.alternativ?.let(::alternativ)
        )

    private fun opptjeningGrunnlag(dto: SimulatorPersonligOpptjeningGrunnlag) =
        SimulertOpptjeningGrunnlag(
            aar = dto.aar,
            pensjonsgivendeInntektBeloep = dto.pensjonsgivendeInntektBeloep
        )

    private fun alternativ(dto: SimulatorPersonligAlternativ) =
        Alternativ(
            gradertUttakAlder = dto.gradertUttakAlder?.let(::alder),
            uttakGrad = dto.uttaksgrad?.let(Uttaksgrad::from),
            heltUttakAlder = alder(dto.heltUttakAlder)
        )

    private fun alder(dto: SimulatorPersonligAlder) =
        Alder(dto.aar, dto.maaneder)

    private fun alderspensjonMaanedsbeloep(dto: SimulatorPersonligMaanedsbeloep) =
        AlderspensjonMaanedsbeloep(
            gradertUttak = dto.gradertUttakBeloep,
            heltUttak = dto.heltUttakBeloep
        )
}
