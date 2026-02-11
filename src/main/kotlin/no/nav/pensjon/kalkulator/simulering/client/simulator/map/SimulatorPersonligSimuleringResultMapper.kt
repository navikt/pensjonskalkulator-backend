package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.*
import no.nav.pensjon.kalkulator.validity.Problem
import no.nav.pensjon.kalkulator.validity.ProblemType

object SimulatorPersonligSimuleringResultMapper {

    fun fromDto(dto: SimulatorPersonligSimuleringResult) =
        SimuleringResult(
            alderspensjon = dto.alderspensjonListe.map(::alderspensjon),
            alderspensjonMaanedsbeloep = dto.alderspensjonMaanedsbeloep?.let(::alderspensjonMaanedsbeloep),
            pre2025OffentligAfp = dto.pre2025OffentligAfp?.let(::tidsbegrensetOffentligAfp),
            afpPrivat = dto.privatAfpListe.map(::privatAfp),
            afpOffentlig = dto.livsvarigOffentligAfpListe.map(::livsvarigOffentligAfp),
            vilkaarsproeving = dto.vilkaarsproeving?.let(::vilkaarsproeving) ?: Vilkaarsproeving(innvilget = true),
            harForLiteTrygdetid = dto.tilstrekkeligTrygdetidForGarantipensjon == false,
            trygdetid = dto.trygdetid ?: 0,
            opptjeningGrunnlagListe = dto.opptjeningGrunnlagListe.orEmpty().map(::opptjeningGrunnlag),
            problem = dto.error?.let(::problem)
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
            skjermingstillegg = dto.skjermingstillegg ?: 0,
            kapittel19Gjenlevendetillegg = dto.kapittel19Gjenlevendetillegg ?: 0
        )

    private fun livsvarigOffentligAfp(dto: SimulatorPersonligLivsvarigOffentligAfp) =
        SimulertAfpOffentlig(
            alder = dto.alderAar,
            beloep = dto.beloep,
            maanedligBeloep = dto.maanedligBeloep
        )

    private fun tidsbegrensetOffentligAfp(dto: SimulatorPre2025OffentligAfp) =
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
            saertillegg = dto.saertillegg,
            afpGrad = dto.afpGrad,
            afpAvkortetTil70Prosent = dto.afpAvkortetTil70Prosent
        )

    private fun privatAfp(dto: SimulatorPersonligPrivatAfp) =
        SimulertAfpPrivat(
            alder = dto.alderAar,
            beloep = dto.beloep,
            kompensasjonstillegg = dto.kompensasjonstillegg,
            kronetillegg = dto.kronetillegg,
            livsvarig = dto.livsvarig,
            maanedligBeloep = dto.maanedligBeloep ?: 0
        )

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
        Alder(aar = dto.aar, maaneder = dto.maaneder)

    private fun alderspensjonMaanedsbeloep(dto: SimulatorPersonligMaanedsbeloep) =
        AlderspensjonMaanedsbeloep(
            gradertUttak = dto.gradertUttakBeloep,
            heltUttak = dto.heltUttakBeloep
        )

    private fun problem(dto: SimulatorPersonligSimuleringError) =
        Problem(
            type = problemType(dto),
            beskrivelse = "${dto.message} (${dto.exception})"
        )

    private fun problemType(dto: SimulatorPersonligSimuleringError): ProblemType =
        when (dto.exception) {
            "FeilISimuleringsgrunnlagetException" -> ProblemType.ANNEN_KLIENTFEIL
            "ImplementationUnrecoverableException" -> ProblemType.ANNEN_KLIENTFEIL
            "IllegalArgumentException" -> ProblemType.ANNEN_KLIENTFEIL
            "InvalidArgumentException" -> ProblemType.ANNEN_KLIENTFEIL
            "KanIkkeBeregnesException" -> ProblemType.ANNEN_KLIENTFEIL
            "KonsistensenIGrunnlagetErFeilException" -> ProblemType.ANNEN_KLIENTFEIL
            "PersonForGammelException" -> ProblemType.PERSON_FOR_HOEY_ALDER
            "PersonForUngException" -> ProblemType.ANNEN_KLIENTFEIL
            "Pre2025OffentligAfpAvslaattException" -> ProblemType.ANNEN_KLIENTFEIL
            "RegelmotorValideringException" -> ProblemType.ANNEN_KLIENTFEIL
            "UtilstrekkeligOpptjeningException" -> ProblemType.UTILSTREKKELIG_OPPTJENING
            "UtilstrekkeligTrygdetidException" -> ProblemType.UTILSTREKKELIG_TRYGDETID
            "EgressException" -> ProblemType.SERVERFEIL // feil i simulatoren
            "BadRequestException" -> ProblemType.ANNEN_KLIENTFEIL
            "InvalidEnumValueException" -> ProblemType.SERVERFEIL // feil i denne backend
            else -> ProblemType.SERVERFEIL
        }
}
