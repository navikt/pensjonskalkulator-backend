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

object UttaksalderMapperV1 {

    fun toDto(uttaksalder: Alder?): AlderDto? = uttaksalder?.let(::alderDto)

    fun fromIngressSpecForHeltUttakV1(spec: IngressUttaksalderSpecForHeltUttakV1) =
        ImpersonalUttaksalderSpec(
            sivilstand = spec.sivilstand,
            harEps = spec.harEps,
            aarligInntektFoerUttak = spec.aarligInntektFoerUttakBeloep,
            simuleringType = spec.simuleringstype ?: SimuleringType.ALDERSPENSJON,
            gradertUttak = null,
            heltUttak = spec.aarligInntektVsaPensjon?.let(::heltUttakV1),
            utenlandsperiodeListe = spec.utenlandsperiodeListe.orEmpty().map(::utenlandsperiodeSpecV1)
        )

    private fun heltUttakV1(spec: IngressUttaksalderInntektV1) =
        HeltUttak(
            uttakFomAlder = null, // this is the value to be found
            inntekt = inntektV1(spec)
        )

    private fun inntektV1(spec: IngressUttaksalderInntektV1) =
        Inntekt(
            aarligBeloep = spec.beloep,
            tomAlder = spec.sluttAlder?.let(::alder) ?: defaultHeltUttakInntektTomAlder
        )

    private fun utenlandsperiodeSpecV1(dto: UttaksalderUtenlandsperiodeSpecV1) =
        Opphold(
            fom = dto.fom,
            tom = dto.tom,
            land = Land.valueOf(dto.landkode),
            arbeidet = dto.arbeidetUtenlands
        )

    private fun alder(dto: IngressUttaksalderAlderV1) = Alder(dto.aar, dto.maaneder)

    private fun alderDto(alder: Alder) = AlderDto(alder.aar, alder.maaneder)
}
