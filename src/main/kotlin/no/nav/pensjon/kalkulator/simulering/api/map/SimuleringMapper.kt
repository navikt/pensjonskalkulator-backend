package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import java.time.LocalDate

object SimuleringMapper {

    fun resultatDto(resultat: Simuleringsresultat) =
        SimuleringsresultatDto(
            alderspensjon = resultat.alderspensjon.map { PensjonsberegningDto(it.alder, it.beloep) },
            afpPrivat = resultat.afpPrivat.map { PensjonsberegningDto(it.alder, it.beloep) },
            vilkaarErOppfylt = true
        )

    // V1
    fun fromSpecDto(spec: SimuleringSpecDto) =
        ImpersonalSimuleringSpec(
            simuleringType = spec.simuleringstype,
            foersteUttakAlder = alder(spec.foersteUttaksalder),
            foedselDato = spec.foedselsdato,
            epsHarInntektOver2G = spec.epsHarInntektOver2G,
            forventetInntekt = spec.forventetInntekt,
            sivilstand = spec.sivilstand
        )

    fun fromIngressSpecDto(spec: SimuleringIngressSpecDto) =
        ImpersonalSimuleringSpec(
            simuleringType = spec.simuleringstype,
            foersteUttakAlder = alder(spec.foersteUttaksalder),
            foedselDato = spec.foedselsdato,
            epsHarInntektOver2G = spec.epsHarInntektOver2G,
            forventetInntekt = spec.forventetInntekt,
            sivilstand = spec.sivilstand,
            gradertUttak = spec.gradertUttak?.let { gradertUttak(it, spec.foedselsdato) }
        )

    private fun alder(dto: AlderIngressDto) = Alder(dto.aar, dto.maaneder)

    private fun gradertUttak(dto: SimuleringGradertUttakIngressDto, foedselDato: LocalDate) =
        GradertUttak(
            grad = Uttaksgrad.from(dto.uttaksgrad),
            heltUttakAlder = alder(dto.heltUttakAlder),
            inntektUnderGradertUttak = dto.inntektUnderGradertUttak ?: 0,
            foedselDato = foedselDato
        )
}
