package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by the API offered to clients.
 */
object SimuleringMapper {

    private val defaultInntektTomAlder = Alder(99, 11)

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
            gradertUttak = null, // not supported in V1
            heltUttak = HeltUttak(
                uttakFomAlder = alder(spec.foersteUttaksalder),
                inntekt = null // not supported in V1
            )
        )

    fun fromIngressSpecDtoV2(spec: SimuleringIngressSpecDtoV2) =
        ImpersonalSimuleringSpec(
            simuleringType = spec.simuleringstype,
            epsHarInntektOver2G = spec.epsHarInntektOver2G,
            forventetAarligInntektFoerUttak = spec.forventetInntekt,
            sivilstand = spec.sivilstand,
            gradertUttak = spec.gradertUttak?.let { gradertUttak(it) },
            heltUttak = heltUttak(spec.heltUttak)
        )

    private fun alder(dto: SimuleringAlderDto) = Alder(dto.aar, dto.maaneder)

    private fun gradertUttak(dto: SimuleringGradertUttakIngressDtoV2) =
        GradertUttak(
            grad = Uttaksgrad.from(dto.grad),
            uttakFomAlder = alder(dto.uttaksalder),
            aarligInntekt = dto.aarligInntekt ?: 0
        )

    private fun heltUttak(dto: SimuleringHeltUttakIngressDtoV2) =
        HeltUttak(
            uttakFomAlder = alder(dto.uttaksalder),
            inntekt = inntekt(dto.aarligInntektVsaPensjon)
        )

    private fun inntekt(dto: SimuleringInntektDtoV2) =
        Inntekt(
            aarligBeloep = dto.beloep,
            tomAlder = dto.sluttAlder?.let(::alder) ?: defaultInntektTomAlder
        )
}
