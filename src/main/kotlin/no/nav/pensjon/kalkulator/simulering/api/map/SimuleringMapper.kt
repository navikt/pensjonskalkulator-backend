package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.api.dto.PensjonsberegningDto
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringsresultatDto
import no.nav.pensjon.kalkulator.simulering.api.dto.Vilkaarsbrudd

object SimuleringMapper {

    fun resultatDto(resultat: Simuleringsresultat) =
        SimuleringsresultatDto(
            alderspensjon = resultat.alderspensjon.map { PensjonsberegningDto(it.alder, it.beloep) },
            afpPrivat = resultat.afpPrivat.map { PensjonsberegningDto(it.alder, it.beloep) }
        )

    fun vilkaarsbruddDto() =
        SimuleringsresultatDto(
            uttakskravIkkeOppfylt = listOf(Vilkaarsbrudd.FOR_LAVT_TIDLIG_UTTAK)
            //TODO: Deduce Vilkaarsbrudd from PEN response
        )
}
