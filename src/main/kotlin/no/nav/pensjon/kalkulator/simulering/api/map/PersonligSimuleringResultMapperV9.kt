package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import no.nav.pensjon.kalkulator.simulering.api.map.PersonligSimuleringResultUtil.filtrerBortGjeldendeAlderFoerBursdagInnevaerendeMaaned
import java.time.LocalDate

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 9 of the API offered to clients.
 */
object PersonligSimuleringResultMapperV9 {

    fun resultV9(source: SimuleringResult, foedselsdato: LocalDate) =
        PersonligSimuleringResultV9(
            alderspensjon = source.alderspensjon.map(::alderspensjon)
                .let { justerAlderspensjonInnevaerendeAar(it, foedselsdato) }
                .let {
                    filtrerBortGjeldendeAlderFoerBursdagInnevaerendeMaaned(
                        it,
                        foedselsdato,
                        PersonligSimuleringAlderspensjonResultV9::alder
                    )
                },
            alderspensjonMaanedligVedEndring = maanedligPensjon(source.alderspensjonMaanedsbeloep),
            pre2025OffentligAfp = source.pre2025OffentligAfp?.let(::pre2025OffentligAfp),
            afpPrivat = source.afpPrivat.map(::privatAfp)
                .let { justerPrivatAfpInnevaerendeAar(it, foedselsdato) }
                .let {
                    filtrerBortGjeldendeAlderFoerBursdagInnevaerendeMaaned(
                        it,
                        foedselsdato,
                        PersonligSimuleringAfpPrivatResultV9::alder
                    )
                },
            afpOffentlig = source.afpOffentlig.map(::offentligAfp),
            vilkaarsproeving = vilkaarsproeving(source.vilkaarsproeving),
            harForLiteTrygdetid = source.harForLiteTrygdetid,
        )

    private fun alderspensjon(source: SimulertAlderspensjon) =
        PersonligSimuleringAlderspensjonResultV9(
            alder = source.alder,
            beloep = source.beloep
        )

    /**
     * Assign a pension with age 0 to the current age, or remove it from the list if the current age already exists.
     */
    fun justerAlderspensjonInnevaerendeAar(
        alderspensjonList: List<PersonligSimuleringAlderspensjonResultV9>,
        foedselsdato: LocalDate
    ): List<PersonligSimuleringAlderspensjonResultV9> =
        alderspensjonList
            .firstOrNull { it.alder == 0 }
            ?.let {
                val innevaerendeAarAlder = Alder.from(foedselsdato, LocalDate.now()).aar
                val oppdatertAlderspensjonList = alderspensjonList.filter { it.alder != 0 }.toMutableList()

                if (oppdatertAlderspensjonList.any { it.alder == innevaerendeAarAlder }) {
                    return oppdatertAlderspensjonList.sortedBy { it.alder }
                }
                oppdatertAlderspensjonList.add(
                    PersonligSimuleringAlderspensjonResultV9(
                        innevaerendeAarAlder,
                        it.beloep,
                        it.inntektspensjonBeloep,
                        it.garantipensjonBeloep,
                        it.delingstall,
                        it.pensjonBeholdningFoerUttakBeloep,
                        it.andelsbroekKap19,
                        it.andelsbroekKap20,
                        it.sluttpoengtall,
                        it.trygdetidKap19,
                        it.trygdetidKap20,
                        it.poengaarFoer92,
                        it.poengaarEtter91,
                        it.forholdstall,
                        it.grunnpensjon,
                        it.tilleggspensjon,
                        it.pensjonstillegg,
                        it.skjermingstillegg
                    )
                )
                oppdatertAlderspensjonList.sortedBy { it.alder }
            } ?: alderspensjonList

    /**
     * Assign a Afp Privat with age 0 to the current age, or remove it from the list if the current age already exists.
     */
    fun justerPrivatAfpInnevaerendeAar(
        afpListe: List<PersonligSimuleringAfpPrivatResultV9>,
        foedselsdato: LocalDate
    ): List<PersonligSimuleringAfpPrivatResultV9> =
        afpListe
            .firstOrNull { it.alder == 0 }
            ?.let {
                val innevaerendeAarAlder = Alder.from(foedselsdato, LocalDate.now()).aar
                val oppdatertAfpListe = afpListe.filter { it.alder != 0 }.toMutableList()

                if (oppdatertAfpListe.any { it.alder == innevaerendeAarAlder }) {
                    return oppdatertAfpListe.sortedBy { it.alder }
                }

                oppdatertAfpListe.add(
                    PersonligSimuleringAfpPrivatResultV9(
                        innevaerendeAarAlder,
                        it.beloep,
                        it.kompensasjonstillegg,
                        it.kronetillegg,
                        it.livsvarig,
                        it.maanedligBeloep
                    )
                )
                oppdatertAfpListe.sortedBy { it.alder }
            } ?: afpListe

    private fun maanedligPensjon(source: AlderspensjonMaanedsbeloep?) =
        PersonligSimuleringMaanedligPensjonResultV9(
            gradertUttakMaanedligBeloep = source?.gradertUttak,
            heltUttakMaanedligBeloep = source?.heltUttak ?: 0
        )

    private fun pre2025OffentligAfp(source: SimulertPre2025OffentligAfp) =
        PersonligSimuleringPre2025OffentligAfpResultV9(
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
        PersonligSimuleringAfpPrivatResultV9(
            alder = source.alder,
            beloep = source.beloep,
            kompensasjonstillegg = source.kompensasjonstillegg,
            kronetillegg = source.kronetillegg,
            livsvarig = source.livsvarig,
            maanedligBeloep = source.maanedligBeloep
        )

    private fun offentligAfp(source: SimulertAfpOffentlig) =
        PersonligSimuleringAarligPensjonResultV9(
            alder = source.alder,
            beloep = source.beloep,
            maanedligBeloep = source.maanedligBeloep
        )

    private fun vilkaarsproeving(source: Vilkaarsproeving) =
        PersonligSimuleringVilkaarsproevingResultV9(
            vilkaarErOppfylt = source.innvilget,
            alternativ = source.alternativ?.let(::alternativ)
        )

    private fun alternativ(source: Alternativ) =
        PersonligSimuleringAlternativResultV9(
            gradertUttaksalder = source.gradertUttakAlder?.let(::alder),
            uttaksgrad = prosentsats(source.uttakGrad),
            heltUttaksalder = alder(source.heltUttakAlder)
        )

    private fun prosentsats(grad: Uttaksgrad?): Int? =
        grad?.let {
            if (it == Uttaksgrad.HUNDRE_PROSENT) null else it.prosentsats
        }

    private fun alder(source: Alder) =
        PersonligSimuleringAlderResultV9(aar = source.aar, maaneder = source.maaneder)
}
