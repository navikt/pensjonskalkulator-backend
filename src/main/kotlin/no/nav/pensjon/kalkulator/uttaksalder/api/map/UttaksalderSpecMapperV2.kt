package no.nav.pensjon.kalkulator.uttaksalder.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.HeltUttak.Companion.defaultHeltUttakInntektTomAlder
import no.nav.pensjon.kalkulator.general.Inntekt
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.*

/**
 * Maps from data transfer object (DTO) to domain object.
 * The DTO represent version 2 of specification for 'tidligst mulige uttaksalder for helt uttak'.
 */
object UttaksalderSpecMapperV2 {

    fun fromDtoV2(source: UttaksalderSpecV2) =
        ImpersonalUttaksalderSpec(
            sivilstand = source.sivilstand,
            harEps = source.epsHarInntektOver2G || source.epsHarPensjon,
            aarligInntektFoerUttak = source.aarligInntektFoerUttakBeloep,
            simuleringType = source.simuleringstype ?: SimuleringType.ALDERSPENSJON,
            gradertUttak = null,
            heltUttak = source.aarligInntektVsaPensjon?.let(::heltUttak),
            utenlandsperiodeListe = source.utenlandsperiodeListe.orEmpty().map(::utenlandsperiode)
        )

    private fun heltUttak(source: UttaksalderInntektSpecV2) =
        HeltUttak(
            uttakFomAlder = null, // this is the value to be found
            inntekt = inntekt(source)
        )

    private fun inntekt(source: UttaksalderInntektSpecV2) =
        Inntekt(
            aarligBeloep = source.beloep,
            tomAlder = source.sluttAlder?.let(::alder) ?: defaultHeltUttakInntektTomAlder
        )

    private fun utenlandsperiode(source: UttaksalderUtenlandsperiodeSpecV2) =
        Opphold(
            fom = source.fom,
            tom = source.tom,
            land = Land.valueOf(source.landkode),
            arbeidet = source.arbeidetUtenlands
        )

    private fun alder(source: UttaksalderAlderSpecV2) =
        Alder(aar = source.aar, maaneder = source.maaneder)
}
