package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*

/**
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 * The DTOs are specified by version 9 of the API offered to clients.
 */
object PersonligSimuleringSpecMapperV9 {

    fun fromSpecV9(source: PersonligSimuleringSpecV9) =
        ImpersonalSimuleringSpec(
            simuleringType = source.simuleringstype,
            eps = eps(source),
            forventetAarligInntektFoerUttak = source.aarligInntektFoerUttakBeloep,
            sivilstand = source.sivilstand,
            gradertUttak = source.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(source.heltUttak),
            utenlandsopphold = utenlandsopphold(source),
            afpInntektMaanedFoerUttak = source.afpInntektMaanedFoerUttak,
            afpOrdning = source.afpOrdning,
            innvilgetLivsvarigOffentligAfp = source.innvilgetLivsvarigOffentligAfp?.firstOrNull()?.let(::afp)
        )

    private fun eps(dto: PersonligSimuleringSpecV9) =
        EpsSpec(
            levende = LevendeEps(
                harInntektOver2G = dto.epsHarInntektOver2G == true,
                harPensjon = dto.epsHarPensjon == true
            )
        )

    private fun gradertUttak(source: PersonligSimuleringGradertUttakSpecV9) =
        GradertUttak(
            grad = Uttaksgrad.from(source.grad),
            uttakFomAlder = alder(source.uttaksalder),
            aarligInntekt = source.aarligInntektVsaPensjonBeloep ?: 0
        )

    private fun heltUttak(source: PersonligSimuleringHeltUttakSpecV9) =
        HeltUttak(
            uttakFomAlder = alder(source.uttaksalder),
            inntekt = source.aarligInntektVsaPensjon?.let(::inntekt)
        )

    private fun utenlandsopphold(source: PersonligSimuleringSpecV9) =
        Utenlandsopphold(
            periodeListe = source.utenlandsperiodeListe.orEmpty().map(::opphold),
            antallAar = 0 // not relevant when utenlandsperiodeListe used
        )

    private fun opphold(source: PersonligSimuleringUtenlandsperiodeSpecV9) =
        Opphold(
            fom = source.fom,
            tom = source.tom,
            land = Land.valueOf(source.landkode),
            arbeidet = source.arbeidetUtenlands
        )

    private fun inntekt(source: PersonligSimuleringInntektSpecV9) =
        Inntekt(
            aarligBeloep = source.beloep,
            tomAlder = source.sluttAlder.let(::alder)
        )

    private fun afp(source: PersonligSimuleringInnvilgetLivsvarigOffentligAfpSpecV9) =
        InnvilgetLivsvarigOffentligAfpSpec(
            aarligBruttoBeloep = source.aarligBruttoBeloep,
            uttakFom = source.uttakFom,
            sistRegulertGrunnbeloep = source.sistRegulertGrunnbeloep
        )

    private fun alder(source: PersonligSimuleringAlderSpecV9) =
        Alder(aar = source.aar, maaneder = source.maaneder)
}
