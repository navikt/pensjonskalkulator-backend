package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.api.dto.PensjonsberegningDto
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringsresultatDto

object SimuleringMapper {

    fun resultatDto(resultat: Simuleringsresultat) =
        SimuleringsresultatDto(
            alderspensjon = resultat.alderspensjon.map { PensjonsberegningDto(it.alder, it.beloep) },
            afpPrivat = resultat.afpPrivat.map { PensjonsberegningDto(it.alder, it.beloep) },
            vilkaarErOppfylt = true
        )

    fun vilkaarsbruddDto() = SimuleringsresultatDto(vilkaarErOppfylt = false)
}
