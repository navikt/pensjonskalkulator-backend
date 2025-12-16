package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.api.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.simulering.Eps
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.simulering.Utenlandsopphold
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.SimuleringOffentligTjenestepensjonFoer1963Spec
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.api.dto.SimuleringOffentligTjenestepensjonFoer1963SpecV2

object TjenestepensjonSimuleringFoer1963SpecMapperV2 {

    fun fromDtoV2(source: SimuleringOffentligTjenestepensjonFoer1963SpecV2): SimuleringOffentligTjenestepensjonFoer1963Spec {
        return SimuleringOffentligTjenestepensjonFoer1963Spec(
            simuleringType = source.simuleringstype,
            foedselsdato = source.foedselsdato,
            eps = eps(source),
            forventetAarligInntektFoerUttak = source.aarligInntektFoerUttakBeloep,
            sivilstand = source.sivilstand,
            gradertUttak = source.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(source.heltUttak),
            utenlandsopphold = utenlandsopphold(source),
            afpInntektMaanedFoerUttak = source.afpInntektMaanedFoerUttak,
            afpOrdning = source.afpOrdning,
            afpInntektMndForUttak = source.afpInntektMaanedFoerUttak,
            stillingsprosentOffHeltUttak = source.stillingsprosentOffHeltUttak,
            stillingsprosentOffGradertUttak = source.stillingsprosentOffGradertUttak,
        )

    }

    private fun eps(source: SimuleringOffentligTjenestepensjonFoer1963SpecV2) =
        Eps(
            harInntektOver2G = source.epsHarInntektOver2G ?: false,
            harPensjon = source.epsHarPensjon ?: false
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

    private fun utenlandsopphold(source: SimuleringOffentligTjenestepensjonFoer1963SpecV2) =
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

    private fun alder(source: PersonligSimuleringAlderSpecV9) =
        Alder(aar = source.aar, maaneder = source.maaneder)
}
