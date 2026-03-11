package no.nav.pensjon.kalkulator.simulering.api.v1.acl.result

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
            tidsbegrensetOffentligAfp = source.pre2025OffentligAfp?.let(::tidsbegrensetOffentligAfp),
            privatAfpListe = source.afpPrivat.map(::privatAfp)
                .let { justerPrivatAfpListe(pensjonListe = it, naavaerendeAlderAar) },
            livsvarigOffentligAfpListe = source.afpOffentlig.map(::livsvarigOffentligAfp),
            vilkaarsproevingsresultat = vilkaarsproevingsresultat(source.vilkaarsproeving),
            trygdetid = trygdetid(source),
            pensjonsgivendeInntektListe = source.opptjeningGrunnlagListe.map(::inntekt),
            problem = source.problem?.let(::problem)
        )

    private fun alderspensjon(source: SimulertAlderspensjon, mode: MappingMode) =
        SimuleringV1Alderspensjon(
            alderAar = source.alder,
            beloep = source.beloep,
            gjenlevendetillegg = if (mode.mapGjenlevendetillegg) source.kapittel19Gjenlevendetillegg else null,
            extension = if (mode.extended) alderspensjonExtension(source) else null
        )

    private fun alderspensjonExtension(source: SimulertAlderspensjon) =
        SimuleringV1AlderspensjonExtension(
            inntektspensjonBeloep = source.inntektspensjonBeloep,
            garantipensjonBeloep = source.garantipensjonBeloep,
            delingstall = source.delingstall,
            pensjonBeholdningFoerUttakBeloep = source.pensjonBeholdningFoerUttak,
            andelsbroekKap19 = source.andelsbroekKap19,
            andelsbroekKap20 = source.andelsbroekKap20,
            sluttpoengtall = source.sluttpoengtall,
            trygdetidKap19 = source.trygdetidKap19,
            trygdetidKap20 = source.trygdetidKap20,
            poengaarFoer92 = source.poengaarFoer92,
            poengaarEtter91 = source.poengaarEtter91,
            forholdstall = source.forholdstall,
            grunnpensjon = source.grunnpensjon,
            tilleggspensjon = source.tilleggspensjon,
            pensjonstillegg = source.pensjonstillegg,
            skjermingstillegg = source.skjermingstillegg
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
}
