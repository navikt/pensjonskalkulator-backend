package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import java.time.LocalDate

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * Rearranges alderspensjon for current year, when applicable.
 * The DTOs are specified by version 7 of the API offered to clients.
 */
object SimuleringResultMapperV7 {

    fun resultatV7(source: SimuleringResult, foedselsdato: LocalDate) =
        SimuleringResultatV7(
            alderspensjon = source.alderspensjon.map(::alderspensjon)
                .let { justerAlderspensjonIInnevaerendeAar(it, foedselsdato) },
            alderspensjonMaanedligVedEndring = AlderspensjonsMaanedligV7(
                gradertUttakMaanedligBeloep = source.alderspensjonMaanedsbeloep?.gradertUttak,
                heltUttakMaanedligBeloep = source.alderspensjonMaanedsbeloep?.heltUttak ?: 0,
            ),
            afpPrivat = source.afpPrivat.map(::privatAfp),
            afpOffentlig = source.afpOffentlig.map(::offentligAfp),
            vilkaarsproeving = vilkaarsproeving(source.vilkaarsproeving),
            harForLiteTrygdetid = source.harForLiteTrygdetid,
        )

    private fun alderspensjon(source: SimulertAlderspensjon) =
        AlderspensjonsberegningV7(
            source.alder,
            source.beloep
        )

    /**
     * When list contains alderspensjon with age 0,
     * replace it with age of current year and add it back to the list,
     * if alderspensjon for current year already exists, replace it.
     */
    fun justerAlderspensjonIInnevaerendeAar(
        alderspensjonList: List<AlderspensjonsberegningV7>,
        foedselsdato: LocalDate
    ): List<AlderspensjonsberegningV7> {
        alderspensjonList
            .firstOrNull { it.alder == 0 }
            ?.let {
                val innevaerendeAarAlder = Alder.from(foedselsdato, LocalDate.now()).aar
                val oppdatertAlderspensjonList = alderspensjonList
                    .filter { it.alder != 0 }
                    .filter { it.alder != innevaerendeAarAlder}
                    .toMutableList()
                oppdatertAlderspensjonList.add(
                    AlderspensjonsberegningV7(
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

    private fun offentligAfp(source: SimulertAfpOffentlig) =
        PensjonsberegningAfpOffentligV7(source.alder, source.beloep)

    private fun privatAfp(source: SimulertAfpPrivat) =
        PensjonsberegningV7(source.alder, source.beloep)

    private fun vilkaarsproeving(source: Vilkaarsproeving) =
        VilkaarsproevingV7(
            vilkaarErOppfylt = source.innvilget,
            alternativ = source.alternativ?.let(::alternativ)
        )

    private fun alternativ(source: Alternativ) =
        AlternativV7(
            gradertUttaksalder = source.gradertUttakAlder?.let(::alder),
            uttaksgrad = prosentsats(source.uttakGrad),
            heltUttaksalder = alder(source.heltUttakAlder)
        )

    private fun prosentsats(grad: Uttaksgrad?): Int? =
        grad?.let {
            if (it == Uttaksgrad.HUNDRE_PROSENT) null else it.prosentsats
        }

    private fun alder(source: Alder) = AlderV7(source.aar, source.maaneder)
}
