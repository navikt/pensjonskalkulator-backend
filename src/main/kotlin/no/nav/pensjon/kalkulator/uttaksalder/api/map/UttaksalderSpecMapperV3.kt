package no.nav.pensjon.kalkulator.uttaksalder.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.HeltUttak.Companion.defaultHeltUttakInntektTomAlder
import no.nav.pensjon.kalkulator.general.Inntekt
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.simulering.InnvilgetLivsvarigOffentligAfpSpec
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.*

/**
 * Maps from a data transfer object (DTO) to a domain object.
 * The DTO represent version 3 of specification for 'tidligst mulige uttaksalder for helt uttak'.
 */
object UttaksalderSpecMapperV3 {

    fun fromDtoV3(source: UttaksalderSpecV3) =
        ImpersonalUttaksalderSpec(
            sivilstand = source.sivilstand,
            harEps = source.epsHarInntektOver2G || source.epsHarPensjon,
            aarligInntektFoerUttak = source.aarligInntektFoerUttakBeloep,
            simuleringType = source.simuleringstype ?: SimuleringType.ALDERSPENSJON,
            gradertUttak = null,
            heltUttak = source.aarligInntektVsaPensjon?.let(::heltUttak),
            utenlandsperiodeListe = source.utenlandsperiodeListe.orEmpty().map(::utenlandsperiode),
            innvilgetLivsvarigOffentligAfp = source.innvilgetLivsvarigOffentligAfp?.firstOrNull()?.let(::afp)
        )

    private fun heltUttak(source: UttaksalderInntektSpecV3) =
        HeltUttak(
            uttakFomAlder = null, // this is the value to be found
            inntekt = inntekt(source)
        )

    private fun inntekt(source: UttaksalderInntektSpecV3) =
        Inntekt(
            aarligBeloep = source.beloep,
            tomAlder = source.sluttAlder?.let(::alder) ?: defaultHeltUttakInntektTomAlder
        )

    private fun utenlandsperiode(source: UttaksalderUtenlandsperiodeSpecV3) =
        Opphold(
            fom = source.fom,
            tom = source.tom,
            land = Land.valueOf(source.landkode),
            arbeidet = source.arbeidetUtenlands
        )

    private fun afp(source: PersonligSimuleringInnvilgetLivsvarigOffentligAfpSpecV3) =
        InnvilgetLivsvarigOffentligAfpSpec(
            aarligBruttoBeloep = source.aarligBruttoBeloep,
            uttakFom = source.uttakFom,
            sistRegulertGrunnbeloep = source.sistRegulertGrunnbeloep
        )

    private fun alder(source: UttaksalderAlderSpecV3) =
        Alder(aar = source.aar, maaneder = source.maaneder)
}
