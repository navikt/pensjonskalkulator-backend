package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.api.dto.PensjonsberegningDto
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringAlderDto
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringSpecDto
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringsresultatDto

object SimuleringMapper {

    fun resultatDto(resultat: Simuleringsresultat) =
        SimuleringsresultatDto(
            alderspensjon = resultat.alderspensjon.map { PensjonsberegningDto(it.alder, it.beloep) },
            afpPrivat = resultat.afpPrivat.map { PensjonsberegningDto(it.alder, it.beloep) },
            vilkaarErOppfylt = true
        )

    fun fromSpecDto(spec: SimuleringSpecDto) =
        ImpersonalSimuleringSpec(
            simuleringType = spec.simuleringstype,
            uttaksgrad = Uttaksgrad.from(spec.uttaksgrad),
            foersteUttaksalder = alder(spec.foersteUttaksalder),
            foedselsdato = spec.foedselsdato,
            epsHarInntektOver2G = spec.epsHarInntektOver2G,
            forventetInntekt = spec.forventetInntekt,
            sivilstand = spec.sivilstand
        )

    private fun alder(dto: SimuleringAlderDto) = Alder(dto.aar, dto.maaneder)
}
