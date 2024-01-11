package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
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
            epsHarInntektOver2G = spec.epsHarInntektOver2G,
            forventetAarligInntektFoerUttak = spec.forventetInntekt,
            sivilstand = spec.sivilstand,
            // gradertUttak not supported in V1
            heltUttak = HeltUttak(
                uttakFomAlder = alder(spec.foersteUttaksalder),
                aarligInntekt = 0, // not supported in V1
                inntektTomAlder = Alder(99, 11), // not supported in V1
                foedselDato = spec.foedselsdato
            )
        )

    fun fromIngressSpecDto(spec: SimuleringIngressSpecDto) =
        ImpersonalSimuleringSpec(
            simuleringType = spec.simuleringstype,
            epsHarInntektOver2G = spec.epsHarInntektOver2G,
            forventetAarligInntektFoerUttak = spec.forventetInntekt,
            sivilstand = spec.sivilstand,
            gradertUttak = spec.gradertUttak?.let { gradertUttak(it, spec.foedselsdato) },
            heltUttak = heltUttak(spec.heltUttak, spec.foedselsdato)
        )

    private fun alder(dto: AlderIngressDto) = Alder(dto.aar, dto.maaneder)

    private fun gradertUttak(dto: SimuleringGradertUttakIngressDto, foedselDato: LocalDate) =
        GradertUttak(
            grad = Uttaksgrad.from(dto.grad),
            uttakFomAlder = alder(dto.uttakFomAlder),
            aarligInntekt = dto.aarligInntektVsaPensjon ?: 0,
            foedselDato = foedselDato
        )

    private fun heltUttak(dto: SimuleringHeltUttakIngressDto, foedselDato: LocalDate) =
        HeltUttak(
            uttakFomAlder = alder(dto.uttakFomAlder),
            aarligInntekt = dto.aarligInntektVsaPensjon,
            inntektTomAlder = alder(dto.inntektTomAlder),
            foedselDato = foedselDato
        )
}
