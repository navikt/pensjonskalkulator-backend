package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 6 of the API offered to clients.
 */
object SimuleringMapperV6 {

    fun fromIngressSimuleringSpecV6(dto: IngressSimuleringSpecV6) =
        ImpersonalSimuleringSpec(
            simuleringType = dto.simuleringstype,
            eps = eps(dto),
            forventetAarligInntektFoerUttak = dto.aarligInntektFoerUttakBeloep,
            sivilstand = dto.sivilstand,
            gradertUttak = dto.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(dto.heltUttak),
            utenlandsopphold = utenlandsopphold(dto)
        )

    private fun eps(dto: IngressSimuleringSpecV6) =
        Eps(
            harInntektOver2G = dto.epsHarInntektOver2G,
            harPensjon = false
        )

    fun resultatV6(source: Simuleringsresultat) =
        SimuleringResultatV6(
            alderspensjon = source.alderspensjon.map { PensjonsberegningV6(it.alder, it.beloep) },
            afpPrivat = source.afpPrivat.map { PensjonsberegningV6(it.alder, it.beloep) },
            afpOffentlig = source.afpOffentlig.map { PensjonsberegningAfpOffentligV6(it.alder, it.beloep) },
            vilkaarsproeving = vilkaarsproeving(source.vilkaarsproeving)
        )

    private fun gradertUttak(dto: IngressSimuleringGradertUttakV6) =
        GradertUttak(
            grad = Uttaksgrad.from(dto.grad),
            uttakFomAlder = alder(dto.uttaksalder),
            aarligInntekt = dto.aarligInntektVsaPensjonBeloep ?: 0
        )

    private fun heltUttak(dto: IngressSimuleringHeltUttakV6) =
        HeltUttak(
            uttakFomAlder = alder(dto.uttaksalder),
            inntekt = dto.aarligInntektVsaPensjon?.let(::inntekt)
        )

    private fun utenlandsopphold(dto: IngressSimuleringSpecV6) =
        Utenlandsopphold(
            periodeListe = dto.utenlandsperiodeListe.orEmpty().map(::opphold),
            antallAar = 0 // not relevant when utenlandsperiodeListe used
        )

    private fun opphold(dto: UtenlandsperiodeSpecV6) =
        Opphold(
            fom = dto.fom,
            tom = dto.tom,
            land = Land.valueOf(dto.landkode),
            arbeidet = dto.arbeidetUtenlands
        )

    private fun inntekt(dto: IngressSimuleringInntektV6) =
        Inntekt(
            aarligBeloep = dto.beloep,
            tomAlder = dto.sluttAlder.let(::alder)
        )

    private fun alder(dto: IngressSimuleringAlderV6) = Alder(dto.aar, dto.maaneder)

    private fun vilkaarsproeving(source: Vilkaarsproeving) =
        VilkaarsproevingV6(
            vilkaarErOppfylt = source.innvilget,
            alternativ = source.alternativ?.let(::alternativ)
        )

    private fun alternativ(source: Alternativ) =
        AlternativV6(
            gradertUttaksalder = source.gradertUttakAlder?.let(::alder),
            uttaksgrad = prosentsats(source.uttakGrad),
            heltUttaksalder = alder(source.heltUttakAlder)
        )

    private fun prosentsats(grad: Uttaksgrad?): Int? =
        grad?.let {
            if (it == Uttaksgrad.HUNDRE_PROSENT) null else it.prosentsats
        }

    private fun alder(source: Alder) = AlderV6(source.aar, source.maaneder)
}
