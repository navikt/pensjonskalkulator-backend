package no.nav.pensjon.kalkulator.simulering.api.intern.v1.acl.result

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.map.PersonligSimuleringResultUtil.filtrerBortGjeldendeAlderFoerBursdagInnevaerendeMaaned
import no.nav.pensjon.kalkulator.validity.Problem
import java.time.LocalDate

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 */
object SimuleringResultMapper {

    fun toDto(source: SimuleringResult, foedselsdato: LocalDate) =
        SimuleringResultDto(
            alderspensjon = source.alderspensjon.map(::alderspensjon)
                .let { justerAlderspensjonInnevaerendeAar(alderspensjonList = it, foedselsdato) }
                .let {
                    filtrerBortGjeldendeAlderFoerBursdagInnevaerendeMaaned(
                        list = it,
                        foedselsdato,
                        alderExtractor = AlderspensjonDto::alderAar
                    )
                },
            maanedligAlderspensjonVedEndring = maanedligPensjon(source.alderspensjonMaanedsbeloep),
            tidsbegrensetOffentligAfp = source.pre2025OffentligAfp?.let(::tidsbegrensetOffentligAfp),
            privatAfp = source.afpPrivat.map(::privatAfp)
                .let { justerPrivatAfpInnevaerendeAar(afpListe = it, foedselsdato) }
                .let {
                    filtrerBortGjeldendeAlderFoerBursdagInnevaerendeMaaned(
                        list = it,
                        foedselsdato,
                        alderExtractor = PrivatAfpDto::alderAar
                    )
                },
            livsvarigOffentligAfp = source.afpOffentlig.map(::livsvarigOffentligAfp),
            vilkaarsproevingsresultat = vilkaarsproevingsresultat(source.vilkaarsproeving),
            trygdetid = TrygdetidDto(antallAar = source.trygdetid, erUtilstrekkelig = source.harForLiteTrygdetid),
            opptjeningsgrunnlagListe = source.opptjeningGrunnlagListe.map(::inntekt),
            problem = source.problem?.let(::problem)
        )

    /**
     * Assign a pension with age 0 to the current age or remove it from the list if the current age already exists.
     */
    private fun justerAlderspensjonInnevaerendeAar(
        alderspensjonList: List<AlderspensjonDto>,
        foedselsdato: LocalDate
    ): List<AlderspensjonDto> =
        alderspensjonList
            .firstOrNull { it.alderAar == 0 }
            ?.let {
                val innevaerendeAarAlder = Alder.from(foedselsdato, LocalDate.now()).aar
                val oppdatertAlderspensjonList = alderspensjonList.filter { it.alderAar != 0 }.toMutableList()

                if (oppdatertAlderspensjonList.any { it.alderAar == innevaerendeAarAlder }) {
                    return oppdatertAlderspensjonList.sortedBy { it.alderAar }
                }
                oppdatertAlderspensjonList.add(
                    AlderspensjonDto(
                        innevaerendeAarAlder,
                        it.beloep,
                        it.gjenlevendetillegg
                    )
                )
                oppdatertAlderspensjonList.sortedBy { it.alderAar }
            } ?: alderspensjonList

    /**
     * Assign privat AFP with age 0 to the current age or remove it from the list if the current age already exists.
     */
    private fun justerPrivatAfpInnevaerendeAar(
        afpListe: List<PrivatAfpDto>,
        foedselsdato: LocalDate
    ): List<PrivatAfpDto> =
        afpListe
            .firstOrNull { it.alderAar == 0 }
            ?.let {
                val innevaerendeAarAlder = Alder.from(foedselsdato, LocalDate.now()).aar
                val oppdatertAfpListe = afpListe.filter { it.alderAar != 0 }.toMutableList()

                if (oppdatertAfpListe.any { it.alderAar == innevaerendeAarAlder }) {
                    return oppdatertAfpListe.sortedBy { it.alderAar }
                }

                oppdatertAfpListe.add(
                    PrivatAfpDto(
                        innevaerendeAarAlder,
                        it.aarligBeloep,
                        it.kompensasjonstillegg,
                        it.kronetillegg,
                        it.livsvarig,
                        it.maanedligBeloep
                    )
                )
                oppdatertAfpListe.sortedBy { it.alderAar }
            } ?: afpListe

    private fun alderspensjon(source: SimulertAlderspensjon) =
        AlderspensjonDto(
            alderAar = source.alder,
            beloep = source.beloep,
            gjenlevendetillegg = source.kapittel19Gjenlevendetillegg
        )

    private fun maanedligPensjon(source: AlderspensjonMaanedsbeloep?) =
        MaanedligPensjonDto(
            gradertUttakMaanedligBeloep = source?.gradertUttak,
            heltUttakMaanedligBeloep = source?.heltUttak ?: 0
        )

    private fun livsvarigOffentligAfp(source: SimulertAfpOffentlig) =
        AarligPensjonDto(
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
        AarligInntektDto(
            aarstall = source.aar,
            pensjonsgivendeInntektBeloep = source.pensjonsgivendeInntektBeloep
        )

    private fun alternativ(source: Alternativ) =
        AlternativDto(
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
