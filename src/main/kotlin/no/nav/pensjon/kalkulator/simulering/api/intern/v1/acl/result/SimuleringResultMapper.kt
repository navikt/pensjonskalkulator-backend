package no.nav.pensjon.kalkulator.simulering.api.intern.v1.acl.result

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.validity.Problem

/**
 * Maps domain objects to data transfer objects (DTOs).
 * The domain objects represent the result of a 'pensjonssimulering'.
 */
object SimuleringResultMapper {

    fun toDto(source: SimuleringResult) =
        SimuleringResultDto(
            alderspensjonListe = source.alderspensjon.map(::alderspensjon),
            tidsbegrensetOffentligAfp = source.pre2025OffentligAfp?.let(::tidsbegrensetOffentligAfp),
            privatAfpListe = source.afpPrivat.map(::privatAfp),
            livsvarigOffentligAfpListe = source.afpOffentlig.map(::livsvarigOffentligAfp),
            vilkaarsproevingsresultat = vilkaarsproevingsresultat(source.vilkaarsproeving),
            trygdetid = TrygdetidDto(antallAar = source.trygdetid, erUtilstrekkelig = source.harForLiteTrygdetid),
            pensjonsgivendeInntektListe = source.opptjeningGrunnlagListe.map(::inntekt),
            problem = source.problem?.let(::problem)
        )

    private fun alderspensjon(source: SimulertAlderspensjon) =
        AlderspensjonDto(
            alderAar = source.alder,
            beloep = source.beloep,
            gjenlevendetillegg = source.kapittel19Gjenlevendetillegg
        )

    private fun livsvarigOffentligAfp(source: SimulertAfpOffentlig) =
        AldersbestemtUtbetalingDto(
            alderAar = source.alder,
            aarligBeloep = source.beloep,
            maanedligBeloep = source.maanedligBeloep
        )

    private fun tidsbegrensetOffentligAfp(source: SimulertPre2025OffentligAfp) =
        TidsbegrensetOffentligAfpDto(
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
        PrivatAfpDto(
            alderAar = source.alder,
            aarligBeloep = source.beloep,
            kompensasjonstillegg = source.kompensasjonstillegg,
            kronetillegg = source.kronetillegg,
            livsvarig = source.livsvarig,
            maanedligBeloep = source.maanedligBeloep
        )

    private fun vilkaarsproevingsresultat(source: Vilkaarsproeving) =
        VilkaarsproevingsresultatDto(
            erInnvilget = source.innvilget,
            alternativ = source.alternativ?.let(::alternativ)
        )

    private fun inntekt(source: SimulertOpptjeningGrunnlag) =
        AarligBeloepDto(
            aarstall = source.aar,
            beloep = source.pensjonsgivendeInntektBeloep
        )

    private fun alternativ(source: Alternativ) =
        UttaksparametreDto(
            gradertUttakAlder = source.gradertUttakAlder?.let(::alder),
            uttaksgrad = prosentsats(source.uttakGrad),
            heltUttakAlder = alder(source.heltUttakAlder)
        )

    private fun prosentsats(grad: Uttaksgrad?): Int? =
        grad?.let {
            if (it == Uttaksgrad.HUNDRE_PROSENT) null else it.prosentsats
        }

    private fun alder(source: Alder) =
        AlderDto(
            aar = source.aar,
            maaneder = source.maaneder
        )

    private fun problem(source: Problem) =
        ProblemDto(
            kode = ProblemTypeDto.entries.firstOrNull { it.internalValue == source.type } ?: ProblemTypeDto.SERVERFEIL,
            beskrivelse = source.beskrivelse
        )
}
