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
            pre2025OffentligAfp = dto.pre2025OffentligAfp?.let(::pre2025OffentligAfp),
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
            pensjonBeholdningFoerUttak = dto.pensjonBeholdningFoerUttak ?: 0,
            andelsbroekKap19 = dto.andelsbroekKap19 ?: 0.0,
            andelsbroekKap20 = dto.andelsbroekKap20 ?: 0.0,
            sluttpoengtall = dto.sluttpoengtall ?: 0.0,
            trygdetidKap19 = dto.trygdetidKap19 ?: 0,
            trygdetidKap20 = dto.trygdetidKap20 ?: 0,
            poengaarFoer92 = dto.poengaarFoer92 ?: 0,
            poengaarEtter91 = dto.poengaarEtter91 ?: 0,
            forholdstall = dto.forholdstall ?: 0.0,
            grunnpensjon = dto.grunnpensjon ?: 0,
            tilleggspensjon = dto.tilleggspensjon ?: 0,
            pensjonstillegg = dto.pensjonstillegg ?: 0,
            skjermingstillegg = dto.skjermingstillegg ?: 0
        )

    private fun pre2025OffentligAfp(dto: SimulatorPre2025OffentligAfp) =
        SimulertPre2025OffentligAfp(
            alderAar = dto.alderAar,
            totaltAfpBeloep = dto.totaltAfpBeloep,
            tidligereArbeidsinntekt = dto.tidligereArbeidsinntekt,
            grunnbeloep = dto.grunnbeloep,
            sluttpoengtall = dto.sluttpoengtall,
            trygdetid = dto.trygdetid,
            poengaarTom1991 = dto.poengaarTom1991,
            poengaarFom1992 = dto.poengaarFom1992,
            grunnpensjon = dto.grunnpensjon,
            tilleggspensjon = dto.tilleggspensjon,
            afpTillegg = dto.afpTillegg,
            saertillegg = dto.saertillegg
        )

    private fun privatAfp(dto: SimulatorPersonligPrivatAfp) =
        SimulertAfpPrivat(dto.alderAar, dto.beloep, dto.maanedligBeloep ?: 0)

    private fun livsvarigOffentligAfp(dto: SimulatorPersonligLivsvarigOffentligAfp) =
        SimulertAfpOffentlig(dto.alderAar, dto.beloep, dto.maanedligBeloep)

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
