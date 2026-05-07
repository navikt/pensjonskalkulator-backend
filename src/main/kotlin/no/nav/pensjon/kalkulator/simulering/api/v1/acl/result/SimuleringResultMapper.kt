package no.nav.pensjon.kalkulator.simulering.api.v1.acl.result

import no.nav.pensjon.kalkulator.afp.BeregnetAfp
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.v1.acl.result.SimuleringResultAdjuster.justerAlderspensjonListe
import no.nav.pensjon.kalkulator.simulering.api.v1.acl.result.SimuleringResultAdjuster.justerPrivatAfpListe
import no.nav.pensjon.kalkulator.validity.Problem

/**
 * Maps a simulering result from domain representation to DTOs.
 * DTO = data transfer object
 */
object SimuleringResultMapper {

    fun toDto(source: SimuleringResult, naavaerendeAlderAar: Int, mode: MappingMode) =
        SimuleringV1Result(
            alderspensjonListe = source.alderspensjon.map { alderspensjon(source = it, mode) }
                .let { justerAlderspensjonListe(pensjonListe = it, naavaerendeAlderAar) },
            maanedligAlderspensjonVedUttaksendring = source.alderspensjonMaanedsbeloep?.let(::maanedligPensjon),
            tidsbegrensetOffentligAfp = source.pre2025OffentligAfp?.let(::tidsbegrensetOffentligAfp),
            privatAfpListe = source.afpPrivat.map(::privatAfp)
                .let { justerPrivatAfpListe(pensjonListe = it, naavaerendeAlderAar) },
            livsvarigOffentligAfpListe = source.afpOffentlig.map(::livsvarigOffentligAfp),
            vilkaarsproevingsresultat = vilkaarsproevingsresultat(source.vilkaarsproeving),
            trygdetid = trygdetid(source),
            pensjonsgivendeInntektListe = source.opptjeningGrunnlagListe.map(::inntekt),
            problem = source.problem?.let(::problem),
            serviceberegnetAfp = source.serviceberegnetAfpResult?.let(::serviceberegnetAfp)
        )

    private fun alderspensjon(source: SimulertAlderspensjon, mode: MappingMode) =
        if (mode.reduced)
            SimuleringV1Alderspensjon(
                alderAar = source.alder,
                beloep = source.beloep,
                inntektspensjonBeloep = null,
                basispensjonBeloep = null,
                garantipensjonBeloep = null,
                garantipensjonSats = null,
                garantitilleggBeloep = null,
                restpensjonBeloep = null,
                grunnpensjonBeloep = null,
                tilleggspensjonBeloep = null,
                pensjonstillegg = null,
                skjermingstillegg = null,
                gjenlevendetillegg = null,
                minstePensjonsnivaaSats = null,
                delingstall = null,
                forholdstall = null,
                pensjonsbeholdningFoerUttakBeloep = null,
                kapittel19Andel = null,
                kapittel20Andel = null,
                sluttpoengtall = null,
                kapittel19Trygdetid = null,
                kapittel20Trygdetid = null,
                poengaarTom1991 = null,
                poengaarFom1992 = null
            )
        else
            SimuleringV1Alderspensjon(
                alderAar = source.alder,
                beloep = source.beloep,
                inntektspensjonBeloep = source.inntektspensjonBeloep,
                basispensjonBeloep = source.kapittel19Pensjon?.basispensjon,
                garantipensjonBeloep = source.kapittel20Pensjon?.garantipensjon?.aarligBeloep,
                garantipensjonSats = source.kapittel20Pensjon?.garantipensjon?.sats,
                garantitilleggBeloep = source.kapittel20Pensjon?.garantitillegg,
                restpensjonBeloep = source.kapittel19Pensjon?.restpensjon,
                grunnpensjonBeloep = source.grunnpensjon,
                tilleggspensjonBeloep = source.tilleggspensjon,
                pensjonstillegg = source.pensjonstillegg,
                skjermingstillegg = source.skjermingstillegg,
                gjenlevendetillegg = source.kapittel19Pensjon?.gjenlevendetillegg,
                minstePensjonsnivaaSats = source.kapittel19Pensjon?.minstePensjonsnivaaSats,
                delingstall = source.delingstall,
                forholdstall = source.forholdstall,
                pensjonsbeholdningFoerUttakBeloep = source.pensjonBeholdningFoerUttak,
                kapittel19Andel = source.kapittel19Pensjon?.andelsbroek,
                kapittel20Andel = source.kapittel20Pensjon?.andelsbroek,
                sluttpoengtall = source.sluttpoengtall,
                kapittel19Trygdetid = source.kapittel19Pensjon?.trygdetidAntallAar,
                kapittel20Trygdetid = source.kapittel20Pensjon?.trygdetidAntallAar,
                poengaarTom1991 = source.poengaarFoer92,
                poengaarFom1992 = source.poengaarEtter91
            )

    private fun maanedligPensjon(source: AlderspensjonMaanedsbeloep) =
        SimuleringV1Uttaksbeloep(
            gradertUttakMaanedligBeloep = source.gradertUttak,
            heltUttakMaanedligBeloep = source.heltUttak
        )

    private fun livsvarigOffentligAfp(source: SimulertAfpOffentlig) =
        SimuleringV1AldersbestemtUtbetaling(
            alderAar = source.alder,
            aarligBeloep = source.beloep,
            maanedligBeloep = source.maanedligBeloep
        )

    private fun tidsbegrensetOffentligAfp(source: SimulertPre2025OffentligAfp) =
        SimuleringV1TidsbegrensetOffentligAfp(
            alderAar = source.alderAar,
            totaltAfpBeloep = source.totaltAfpBeloep,
            tidligereArbeidsinntekt = source.tidligereArbeidsinntekt,
            grunnbeloep = source.grunnbeloep,
            sluttpoengtall = source.sluttpoengtall,
            trygdetid = source.trygdetid,
            poengaarTom1991 = source.poengaarTom1991,
            poengaarFom1992 = source.poengaarFom1992,
            grunnpensjon = source.grunnpensjon,
            tilleggspensjon = source.tilleggspensjon,
            afpTillegg = source.afpTillegg,
            saertillegg = source.saertillegg,
            afpGrad = source.afpGrad,
            erAvkortet = source.afpAvkortetTil70Prosent
        )

    private fun privatAfp(source: SimulertAfpPrivat) =
        SimuleringV1PrivatAfp(
            alderAar = source.alder,
            aarligBeloep = source.beloep,
            kompensasjonstillegg = source.kompensasjonstillegg,
            kronetillegg = source.kronetillegg,
            livsvarig = source.livsvarig,
            maanedligBeloep = source.maanedligBeloep
        )

    private fun vilkaarsproevingsresultat(source: Vilkaarsproeving) =
        SimuleringV1Vilkaarsproevingsresultat(
            erInnvilget = source.innvilget,
            alternativ = source.alternativ?.let(::alternativ)
        )

    private fun trygdetid(source: SimuleringResult) =
        SimuleringV1Trygdetid(
            antallAar = source.trygdetid,
            erUtilstrekkelig = source.harForLiteTrygdetid
        )

    private fun inntekt(source: SimulertOpptjeningGrunnlag) =
        SimuleringV1AarligBeloep(
            aarstall = source.aar,
            beloep = source.pensjonsgivendeInntektBeloep
        )

    private fun alternativ(source: Alternativ) =
        SimuleringV1Uttaksparametre(
            gradertUttakAlder = source.gradertUttakAlder?.let(::alder),
            uttaksgrad = prosentsats(source.uttakGrad),
            heltUttakAlder = alder(source.heltUttakAlder)
        )

    private fun prosentsats(grad: Uttaksgrad?): Int? =
        grad?.let {
            if (it == Uttaksgrad.HUNDRE_PROSENT) null else it.prosentsats
        }

    private fun alder(source: Alder) =
        SimuleringV1Alder(
            aar = source.aar,
            maaneder = source.maaneder
        )

    private fun problem(source: Problem) =
        SimuleringV1Problem(
            kode = SimuleringV1ProblemType.entries.firstOrNull { it.internalValue == source.type }
                ?: SimuleringV1ProblemType.SERVERFEIL,
            beskrivelse = source.beskrivelse
        )

    private fun serviceberegnetAfp(source: BeregnetAfp) =
        SimuleringV1ServiceberegnetAfp(
            beregnetAfp = beregnetAfp(source)
        )

    private fun beregnetAfp(source: BeregnetAfp) =
        SimuleringV1BeregnetAfp(
            totalbelopAfp = source.totalbelopAfp,
            virkFom = source.virkFom,
            tidligereArbeidsinntekt = source.tidligereArbeidsinntekt,
            grunnbelop = source.grunnbelop,
            sluttpoengtall = source.sluttpoengtall,
            trygdetid = source.trygdetid,
            poengar = source.poengar,
            poeangarF92 = source.poeangarF92,
            poeangarE91 = source.poeangarE91,
            grunnpensjon = source.grunnpensjon,
            tilleggspensjon = source.tilleggspensjon,
            afpTillegg = source.afpTillegg,
            fpp = source.fpp,
            sertillegg = source.sertillegg,
            afpGrad = source.grad,
            erAvkortet = source.erAvkortet
        )
}
