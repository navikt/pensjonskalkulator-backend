package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import no.nav.pensjon.kalkulator.simulering.api.map.PersonligSimuleringResultMapperV8.justerAfpPrivatIInnevaerendeAarV8
import no.nav.pensjon.kalkulator.simulering.api.map.PersonligSimuleringResultMapperV8.justerAlderspensjonIInnevaerendeAarV8
import java.time.LocalDate

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 8 of the API offered to clients.
 */
object PersonligSimuleringExtendedResultMapperV8 {

    fun extendedResultV8(source: SimuleringResult, foedselsdato: LocalDate) =
        PersonligSimuleringResultV8(
            alderspensjon = source.alderspensjon.map(::alderspensjon)
                .let { justerAlderspensjonIInnevaerendeAarV8(it, foedselsdato) },
            alderspensjonMaanedligVedEndring = maanedligPensjon(source.alderspensjonMaanedsbeloep),
            pre2025OffentligAfp = source.pre2025OffentligAfp?.let(::pre2025OffentligAfp),
            afpPrivat = source.afpPrivat.map(::privatAfp).let { justerAfpPrivatIInnevaerendeAarV8(it, foedselsdato) },
            afpOffentlig = source.afpOffentlig.map(::livsvarigOffentligAfp),
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
            skjermingstillegg = source.skjermingstillegg,
            kapittel19Gjenlevendetillegg = source.kapittel19Gjenlevendetillegg
        )

    private fun maanedligPensjon(source: AlderspensjonMaanedsbeloep?) =
        PersonligSimuleringMaanedligPensjonResultV8(
            gradertUttakMaanedligBeloep = source?.gradertUttak,
            heltUttakMaanedligBeloep = source?.heltUttak ?: 0
        )

    private fun pre2025OffentligAfp(source: SimulertPre2025OffentligAfp) =
        PersonligSimuleringPre2025OffentligAfpResultV8(
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
            afpAvkortetTil70Prosent = source.afpAvkortetTil70Prosent
        )

    private fun privatAfp(source: SimulertAfpPrivat) =
        PersonligSimuleringAfpPrivatResultV8(
            alder = source.alder,
            beloep = source.beloep,
            kompensasjonstillegg = source.kompensasjonstillegg,
            kronetillegg = source.kronetillegg,
            livsvarig = source.livsvarig,
            maanedligBeloep = source.maanedligBeloep
        )

    private fun livsvarigOffentligAfp(source: SimulertAfpOffentlig) =
        PersonligSimuleringAarligPensjonResultV8(
            alder = source.alder,
            beloep = source.beloep,
            maanedligBeloep = source.maanedligBeloep
        )

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
