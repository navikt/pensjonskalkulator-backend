package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import java.time.LocalDate

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 8 of the API offered to clients.
 */
object PersonligSimuleringResultMapperV8 {

    fun resultV8(source: SimuleringResult, foedselsdato: LocalDate) =
        PersonligSimuleringResultV8(
            alderspensjon = source.alderspensjon.map(::alderspensjon)
                .let { justerAlderspensjonIInnevaerendeAarV8(it, foedselsdato) }
                .let { filtrerBortGjeldendeAlderFoerBursdag(it, foedselsdato, PersonligSimuleringAlderspensjonResultV8::alder) },
            alderspensjonMaanedligVedEndring = maanedligPensjon(source.alderspensjonMaanedsbeloep),
            pre2025OffentligAfp = source.pre2025OffentligAfp?.let(::pre2025OffentligAfp),
            afpPrivat = source.afpPrivat.map(::privatAfp)
                .let { justerAfpPrivatIInnevaerendeAarV8(it, foedselsdato) }
                .let { filtrerBortGjeldendeAlderFoerBursdag(it, foedselsdato, PersonligSimuleringAfpPrivatResultV8::alder) },
            afpOffentlig = source.afpOffentlig.map(::offentligAfp),
            vilkaarsproeving = vilkaarsproeving(source.vilkaarsproeving),
            harForLiteTrygdetid = source.harForLiteTrygdetid,
        )

    private fun alderspensjon(source: SimulertAlderspensjon) =
        PersonligSimuleringAlderspensjonResultV8(
            alder = source.alder,
            beloep = source.beloep
        )

    fun <T> filtrerBortGjeldendeAlderFoerBursdag(
        list: List<T>,
        foedselsdato: LocalDate,
        alderExtractor: (T) -> Int
    ): List<T> {
        val brukerFyllerSnartAarDenneMaaneden = foedselsdato.monthValue == LocalDate.now().monthValue
                && foedselsdato.dayOfMonth >= LocalDate.now().dayOfMonth
        if (brukerFyllerSnartAarDenneMaaneden) {
            val alderAarTilAaTaBort = Alder.from(foedselsdato, LocalDate.now()).aar
            return list.filter { alderExtractor(it) != alderAarTilAaTaBort }.sortedBy(alderExtractor)
        }
        return list.sortedBy(alderExtractor)
    }

    /**
     * Assign a pension with age 0 to the current age, or remove it from the list if the current age already exists.
     */
    fun justerAlderspensjonIInnevaerendeAarV8(
        alderspensjonList: List<PersonligSimuleringAlderspensjonResultV8>,
        foedselsdato: LocalDate
    ): List<PersonligSimuleringAlderspensjonResultV8> {
        alderspensjonList
            .firstOrNull { it.alder == 0 }
            ?.let {
                val innevaerendeAarAlder = Alder.from(foedselsdato, LocalDate.now()).aar
                val oppdatertAlderspensjonList = alderspensjonList.filter { it.alder != 0 }.toMutableList()

                if (oppdatertAlderspensjonList.any { it.alder == innevaerendeAarAlder }) {
                    return oppdatertAlderspensjonList.sortedBy { it.alder }
                }
                oppdatertAlderspensjonList.add(
                    PersonligSimuleringAlderspensjonResultV8(
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
                return oppdatertAlderspensjonList.sortedBy { it.alder }
            } ?: return alderspensjonList
    }

    /**
     * Assign a Afp Privat with age 0 to the current age, or remove it from the list if the current age already exists.
     */
    fun justerAfpPrivatIInnevaerendeAarV8(
        afpPrivatList: List<PersonligSimuleringAfpPrivatResultV8>,
        foedselsdato: LocalDate
    ): List<PersonligSimuleringAfpPrivatResultV8> {
        afpPrivatList
            .firstOrNull { it.alder == 0 }
            ?.let {
                val innevaerendeAarAlder = Alder.from(foedselsdato, LocalDate.now()).aar
                val oppdatertAfpPrivatList = afpPrivatList.filter { it.alder != 0 }.toMutableList()

                if (oppdatertAfpPrivatList.any { it.alder == innevaerendeAarAlder }) {
                    return oppdatertAfpPrivatList.sortedBy { it.alder }
                }
                oppdatertAfpPrivatList.add(
                    PersonligSimuleringAfpPrivatResultV8(
                        innevaerendeAarAlder,
                        it.beloep,
                        it.kompensasjonstillegg,
                        it.kronetillegg,
                        it.livsvarig,
                        it.maanedligBeloep
                    )
                )
                return oppdatertAfpPrivatList.sortedBy { it.alder }
            } ?: return afpPrivatList
    }

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
        PersonligSimuleringAfpPrivatResultV8(alder = source.alder, beloep = source.beloep, kompensasjonstillegg = source.kompensasjonstillegg, kronetillegg = source.kronetillegg, livsvarig = source.livsvarig, maanedligBeloep = source.maanedligBeloep)

    private fun offentligAfp(source: SimulertAfpOffentlig) =
        PersonligSimuleringAarligPensjonResultV8(alder = source.alder, beloep = source.beloep, maanedligBeloep = source.maanedligBeloep)

    private fun vilkaarsproeving(source: Vilkaarsproeving) =
        PersonligSimuleringVilkaarsproevingResultV8(
            vilkaarErOppfylt = source.innvilget,
            alternativ = source.alternativ?.let(::alternativ)
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
