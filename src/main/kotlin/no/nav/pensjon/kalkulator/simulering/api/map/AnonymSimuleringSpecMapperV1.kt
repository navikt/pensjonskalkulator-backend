package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.general.HeltUttak.Companion.defaultHeltUttakInntektTomAlder
import no.nav.pensjon.kalkulator.simulering.Eps
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Utenlandsopphold
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to 'anonym simulering'.
 * The DTOs are specified by version 1 of the API offered to clients.
 */
object AnonymSimuleringSpecMapperV1 {

    fun fromAnonymSimuleringSpecV1(dto: AnonymSimuleringSpecV1) =
        ImpersonalSimuleringSpec(
            simuleringType = AnonymSimuleringTypeV1.fromExternalValue(dto.simuleringType).internalValue,
            eps = eps(dto),
            forventetAarligInntektFoerUttak = dto.aarligInntektFoerUttakBeloep,
            sivilstand = AnonymSivilstandV1.fromExternalValue(dto.sivilstand).internalValue,
            gradertUttak = dto.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(dto.heltUttak),
            utenlandsopphold = utenlandsopphold(dto),
            foedselAar = dto.foedselAar,
            inntektOver1GAntallAar = dto.inntektOver1GAntallAar
        )

    private fun eps(dto: AnonymSimuleringSpecV1) =
        Eps(
            harInntektOver2G = dto.epsHarInntektOver2G ?: false,
            harPensjon = dto.epsHarPensjon ?: false
        )

    private fun gradertUttak(dto: AnonymSimuleringGradertUttakV1) =
        GradertUttak(
            grad = Uttaksgrad.from(dto.grad),
            uttakFomAlder = alder(dto.uttakAlder),
            aarligInntekt = dto.aarligInntektVsaPensjonBeloep ?: 0
        )

    private fun heltUttak(dto: AnonymSimuleringHeltUttakV1) =
        HeltUttak(
            uttakFomAlder = alder(dto.uttakAlder),
            inntekt = dto.aarligInntektVsaPensjon?.let(::inntekt)
        )

    private fun inntekt(dto: AnonymSimuleringInntektV1) =
        Inntekt(
            aarligBeloep = dto.beloep,
            tomAlder = dto.sluttAlder?.let(::alder) ?: defaultHeltUttakInntektTomAlder
        )

    private fun utenlandsopphold(dto: AnonymSimuleringSpecV1) =
        Utenlandsopphold(
            periodeListe = emptyList(), // not relevant when antallAar used
            antallAar = dto.utenlandsAntallAar
        )

    private fun alder(dto: AnonymSimuleringAlderV1) = Alder(dto.aar, dto.maaneder)
}