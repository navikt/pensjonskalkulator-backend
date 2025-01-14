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
            afpPrivat = source.afpPrivat.map(::privatAfp),
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
                        it.pensjonBeholdningFoerUttakBeloep
                    )
                )
                return oppdatertAlderspensjonList.sortedBy { it.alder }
            } ?: return alderspensjonList
    }

    private fun maanedligPensjon(source: AlderspensjonMaanedsbeloep?) =
        PersonligSimuleringMaanedligPensjonResultV8(
            gradertUttakMaanedligBeloep = source?.gradertUttak,
            heltUttakMaanedligBeloep = source?.heltUttak ?: 0
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
