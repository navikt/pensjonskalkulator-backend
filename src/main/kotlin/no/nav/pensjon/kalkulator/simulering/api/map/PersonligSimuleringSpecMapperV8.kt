package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 8 of the API offered to clients.
 */
object PersonligSimuleringSpecMapperV8 {

    fun fromSpecV8(source: PersonligSimuleringSpecV8) =
        ImpersonalSimuleringSpec(
            simuleringType = source.simuleringstype,
            eps = eps(source),
            forventetAarligInntektFoerUttak = source.aarligInntektFoerUttakBeloep,
            sivilstand = source.sivilstand,
            gradertUttak = source.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(source.heltUttak),
            utenlandsopphold = utenlandsopphold(source)
        )

    private fun eps(source: PersonligSimuleringSpecV8) =
        Eps(
            harInntektOver2G = source.epsHarInntektOver2G,
            harPensjon = false
        )

    private fun gradertUttak(source: PersonligSimuleringGradertUttakSpecV8) =
        GradertUttak(
            grad = Uttaksgrad.from(source.grad),
            uttakFomAlder = alder(source.uttaksalder),
            aarligInntekt = source.aarligInntektVsaPensjonBeloep ?: 0
        )

    private fun heltUttak(source: PersonligSimuleringHeltUttakSpecV8) =
        HeltUttak(
            uttakFomAlder = alder(source.uttaksalder),
            inntekt = source.aarligInntektVsaPensjon?.let(::inntekt)
        )

    private fun utenlandsopphold(source: PersonligSimuleringSpecV8) =
        Utenlandsopphold(
            periodeListe = source.utenlandsperiodeListe.orEmpty().map(::opphold),
            antallAar = 0 // not relevant when utenlandsperiodeListe used
        )

    private fun opphold(source: PersonligSimuleringUtenlandsperiodeSpecV8) =
        Opphold(
            fom = source.fom,
            tom = source.tom,
            land = Land.valueOf(source.landkode),
            arbeidet = source.arbeidetUtenlands
        )

    private fun inntekt(source: PersonligSimuleringInntektSpecV8) =
        Inntekt(
            aarligBeloep = source.beloep,
            tomAlder = source.sluttAlder.let(::alder)
        )

    private fun alder(source: PersonligSimuleringAlderSpecV8) =
        Alder(aar = source.aar, maaneder = source.maaneder)
}
