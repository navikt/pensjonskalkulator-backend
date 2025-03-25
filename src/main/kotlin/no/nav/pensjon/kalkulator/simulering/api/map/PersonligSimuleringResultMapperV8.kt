package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import java.time.LocalDate

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 6 of the API offered to clients.
 */
object PersonligSimuleringResultMapperV8 {

    fun resultV8(source: SimuleringResult, foedselsdato: LocalDate) =
        PersonligSimuleringResultV8(
            alderspensjon = source.alderspensjon.map(::alderspensjon).let { justerAlderspensjonIInnevaerendeAarV8(it, foedselsdato) },
            alderspensjonMaanedligVedEndring = maanedligPensjon(source.alderspensjonMaanedsbeloep),
            pre2025OffentligAfp = source.pre2025OffentligAfp?.let(::pre2025OffentligAfp),
            afpPrivat = source.afpPrivat.map(::privatAfp).let { justerAfpPrivatIInnevaerendeAarV8(it, foedselsdato) },
            afpOffentlig = source.afpOffentlig.map(::offentligAfp),
            vilkaarsproeving = vilkaarsproeving(source.vilkaarsproeving),
            harForLiteTrygdetid = source.harForLiteTrygdetid,
        )

    private fun alderspensjon(source: SimulertAlderspensjon) =
        PersonligSimuleringAlderspensjonResultV8(
            alder = source.alder,
            beloep = source.beloep
        )

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
        afpPrivatList: List<PersonligSimuleringAarligPensjonResultV8>,
        foedselsdato: LocalDate
    ): List<PersonligSimuleringAarligPensjonResultV8> {
        afpPrivatList
            .firstOrNull { it.alder == 0 }
            ?.let {
                val innevaerendeAarAlder = Alder.from(foedselsdato, LocalDate.now()).aar
                val oppdatertAfpPrivatList = afpPrivatList.filter { it.alder != 0 }.toMutableList()

                if (oppdatertAfpPrivatList.any { it.alder == innevaerendeAarAlder }) {
                    return oppdatertAfpPrivatList.sortedBy { it.alder }
                }
                oppdatertAfpPrivatList.add(
                    PersonligSimuleringAarligPensjonResultV8(
                        innevaerendeAarAlder,
                        it.beloep,
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

    private fun pre2025OffentligAfp(source: SimulertPre2025OffentligAfp?) =
        PersonligSimuleringPre2025OffentligAfpResultV8(
            alderAar = source?.alderAar ?: 0,
            totalbelopAfp = source?.totalbelopAfp ?: 0,
            tidligereArbeidsinntekt = source?.tidligereArbeidsinntekt ?: 0,
            grunnbelop = source?.grunnbelop ?: 0,
            sluttpoengtall = source?.sluttpoengtall ?: 0.0,
            trygdetid = source?.trygdetid ?: 0,
            poeangaarFoer92 = source?.poeangaarFoer92 ?: 0,
            poeangaarEtter91 = source?.poeangaarEtter91 ?: 0,
            grunnpensjon = source?.grunnpensjon ?: 0,
            tilleggspensjon = source?.tilleggspensjon ?: 0,
            afpTillegg = source?.afpTillegg ?: 0,
            sertillegg = source?.sertillegg ?: 0
        )

    private fun privatAfp(source: SimulertAfpPrivat) =
        PersonligSimuleringAarligPensjonResultV8(alder = source.alder, beloep = source.beloep)

    private fun offentligAfp(source: SimulertAfpOffentlig) =
        PersonligSimuleringAarligPensjonResultV8(alder = source.alder, beloep = source.beloep)

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
