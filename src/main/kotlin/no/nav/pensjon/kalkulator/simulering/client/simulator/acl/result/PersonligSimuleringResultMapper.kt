package no.nav.pensjon.kalkulator.simulering.client.simulator.acl.result

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.simulator.acl.UttaksgradDto
import no.nav.pensjon.kalkulator.validity.Problem

object PersonligSimuleringResultMapper {

    fun fromDto(dto: PersonligSimuleringResultDto) =
        SimuleringResult(
            alderspensjon = dto.alderspensjonListe.map(::alderspensjon),
            alderspensjonMaanedsbeloep = dto.alderspensjonMaanedsbeloep?.let(::alderspensjonMaanedsbeloep),
            maanedligAlderspensjonForKnekkpunkter = dto.maanedligAlderspensjonForKnekkpunkter?.let(::maanedligAlderspensjonForKnekkpunkter),
            pre2025OffentligAfp = dto.tidsbegrensetOffentligAfp?.let(::tidsbegrensetOffentligAfp),
            afpPrivat = dto.privatAfpListe.map(::privatAfp),
            afpOffentlig = dto.livsvarigOffentligAfpListe.map(::livsvarigOffentligAfp),
            vilkaarsproeving = vilkaarsproeving(dto.vilkaarsproevingsresultat),
            harForLiteTrygdetid = dto.primaerTrygdetid?.erUtilstrekkelig == true,
            trygdetid = dto.primaerTrygdetid?.antallAar ?: 0,
            opptjeningGrunnlagListe = dto.pensjonsgivendeInntektListe.map(::opptjeningGrunnlag),
            problem = dto.problem?.let(::problem)
        )

    private fun alderspensjon(dto: AlderspensjonDto) =
        SimulertAlderspensjon(
            alder = dto.alderAar,
            beloep = dto.beloep,
            inntektspensjonBeloep = dto.inntektspensjon ?: 0,
            delingstall = dto.delingstall ?: 0.0,
            pensjonBeholdningFoerUttak = dto.pensjonsbeholdningFoerUttak ?: 0,
            sluttpoengtall = dto.sluttpoengtall ?: 0.0,
            poengaarFoer92 = dto.poengaarFoer92 ?: 0,
            poengaarEtter91 = dto.poengaarEtter91 ?: 0,
            forholdstall = dto.forholdstall ?: 0.0,
            grunnpensjon = dto.grunnpensjon ?: 0,
            tilleggspensjon = dto.tilleggspensjon ?: 0,
            pensjonstillegg = dto.pensjonstillegg ?: 0,
            skjermingstillegg = dto.skjermingstillegg ?: 0,
            kapittel19Pensjon = dto.kapittel19Pensjon?.let(::kapittel19Pensjon),
            kapittel20Pensjon = dto.kapittel20Pensjon?.let(::kapittel20Pensjon),
        )

    private fun kapittel19Pensjon(dto: Kapittel19PensjonDto) =
        Kapittel19Pensjon(
            andelsbroek = dto.andelsbroek,
            trygdetidAntallAar = dto.trygdetidAntallAar,
            basispensjon = dto.basispensjon,
            restpensjon = dto.restpensjon,
            gjenlevendetillegg = dto.gjenlevendetillegg,
            minstePensjonsnivaaSats = dto.minstePensjonsnivaaSats
        )

    private fun kapittel20Pensjon(dto: Kapittel20PensjonDto) =
        Kapittel20Pensjon(
            andelsbroek = dto.andelsbroek,
            trygdetidAntallAar = dto.trygdetidAntallAar,
            garantipensjon = dto.garantipensjon?.let(::garantipensjon),
            garantitillegg = dto.garantitillegg
        )

    private fun garantipensjon(dto: GarantipensjonDto) =
        Garantipensjon(
            aarligBeloep = dto.aarligBeloep,
            maanedligBeloep = dto.maanedligBeloep,
            sats = dto.sats
        )

    private fun livsvarigOffentligAfp(dto: AldersbestemtUtbetalingDto) =
        SimulertAfpOffentlig(
            alder = dto.alderAar,
            beloep = dto.beloep,
            maanedligBeloep = dto.maanedligBeloep
        )

    private fun tidsbegrensetOffentligAfp(dto: TidsbegrensetOffentligAfpDto) =
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
            afpAvkortetTil70Prosent = dto.erAvkortet
        )

    private fun privatAfp(dto: PrivatAfpDto) =
        SimulertAfpPrivat(
            alder = dto.alderAar,
            beloep = dto.beloep,
            kompensasjonstillegg = dto.kompensasjonstillegg,
            kronetillegg = dto.kronetillegg,
            livsvarig = dto.livsvarig,
            maanedligBeloep = dto.maanedligBeloep
        )

    private fun vilkaarsproeving(dto: VilkaarsproevingsresultatDto) =
        Vilkaarsproeving(
            innvilget = dto.erInnvilget,
            alternativ = dto.alternativ?.let(::alternativ)
        )

    private fun opptjeningGrunnlag(dto: AarligBeloepDto) =
        SimulertOpptjeningGrunnlag(
            aar = dto.aarstall,
            pensjonsgivendeInntektBeloep = dto.beloep
        )

    private fun alternativ(dto: UttaksparametreDto) =
        Alternativ(
            gradertUttakAlder = dto.gradertUttakAlder?.let(::alder),
            uttakGrad = UttaksgradDto.internalValue(dto.uttaksgrad),
            heltUttakAlder = alder(dto.heltUttakAlder)
        )

    private fun alder(dto: AlderDto) =
        Alder(aar = dto.aar, maaneder = dto.maaneder)

    private fun alderspensjonMaanedsbeloep(dto: UttaksbeloepDto) =
        AlderspensjonMaanedsbeloep(
            gradertUttak = dto.gradertUttakBeloep,
            heltUttak = dto.heltUttakBeloep
        )

    private fun maanedligAlderspensjonForKnekkpunkter(dto: MaanedligAlderspensjonForKnekkpunkterDto) =
        SimulertMaanedligAlderspensjonForKnekkpunkter(
            vedGradertUttak = dto.vedGradertUttak?.let(::maanedligAlderspensjon),
            vedHeltUttak = maanedligAlderspensjon(dto.vedHeltUttak),
            vedNormertPensjonsalder = maanedligAlderspensjon(dto.vedNormertPensjonsalder)
        )

    private fun maanedligAlderspensjon(dto: MaanedligAlderspensjonDto) =
        SimulertMaanedligAlderspensjon(
            beloep = dto.beloep,
            inntektspensjonBeloep = dto.inntektspensjon,
            delingstall = dto.delingstall,
            pensjonsbeholdningFoerUttak = dto.pensjonsbeholdningFoerUttak,
            pensjonsbeholdningEtterUttak = dto.pensjonsbeholdningEtterUttak,
            sluttpoengtall = dto.sluttpoengtall,
            poengaarFoer92 = dto.poengaarFoer92,
            poengaarEtter91 = dto.poengaarEtter91,
            forholdstall = dto.forholdstall,
            grunnpensjon = dto.grunnpensjon,
            tilleggspensjon = dto.tilleggspensjon,
            pensjonstillegg = dto.pensjonstillegg,
            skjermingstillegg = dto.skjermingstillegg,
            kapittel19Pensjon = dto.kapittel19Pensjon?.let(::kapittel19Pensjon),
            kapittel20Pensjon = dto.kapittel20Pensjon?.let(::kapittel20Pensjon)
        )

    private fun problem(dto: ProblemDto) =
        Problem(
            type = ProblemTypeDto.internalValue(dto.kode),
            beskrivelse = dto.beskrivelse
        )
}
