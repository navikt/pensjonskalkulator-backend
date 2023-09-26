package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.api.dto.*

object SimuleringMapper {

    fun resultatDto(resultat: Simuleringsresultat) =
        SimuleringsresultatDto(
            alderspensjon = resultat.alderspensjon.map { PensjonsberegningDto(it.alder, it.beloep) },
            afpPrivat = resultat.afpPrivat.map { PensjonsberegningDto(it.alder, it.beloep) },
            vilkaarErOppfylt = true
        )

    fun vilkaarsbruddDto() = SimuleringsresultatDto(vilkaarErOppfylt = false)

    fun fromSpecDto(spec: SimuleringSpecDto) =
        ImpersonalSimuleringSpec(
            spec.simuleringstype,
            Uttaksgrad.from(spec.uttaksgrad),
            alder(spec.foersteUttaksalder),
            spec.foedselsdato,
            spec.epsHarInntektOver2G,
            spec.forventetInntekt,
            spec.sivilstand
        )

    fun fromV0SpecDto(spec: SimuleringSpecV0Dto) =
        ImpersonalSimuleringSpec(
            spec.simuleringstype,
            Uttaksgrad.from(spec.uttaksgrad),
            alder(spec.foersteUttaksalder),
            spec.foedselsdato,
            spec.epsHarInntektOver2G,
            spec.forventetInntekt,
            spec.sivilstand
        )

    private fun alder(dto: SimuleringAlderDto) = Alder(dto.aar, dto.maaneder)

    private fun alder(dto: SimuleringAlderV0Dto) = Alder(dto.aar, dto.maaned)
}
