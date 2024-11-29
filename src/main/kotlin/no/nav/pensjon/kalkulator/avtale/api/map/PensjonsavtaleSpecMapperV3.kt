package no.nav.pensjon.kalkulator.avtale.api.map

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.api.dto.*
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad

object PensjonsavtaleSpecMapperV3 {

    private val alderRepresentingLivsvarig = Alder(aar = 99, maaneder = 11)

    fun fromDtoV3(source: PensjonsavtaleSpecV3) =
        PensjonsavtaleSpec(
            aarligInntektFoerUttak = source.aarligInntektFoerUttakBeloep,
            uttaksperioder = source.uttaksperioder.map(this::uttaksperiodeSpec),
            harEpsPensjon = source.epsHarPensjon,
            harEpsPensjonsgivendeInntektOver2G = source.epsHarInntektOver2G,
            sivilstand = source.sivilstand?.internalValue
        )

    private fun uttaksperiodeSpec(source: PensjonsavtaleUttaksperiodeSpecV3) =
        UttaksperiodeSpec(
            startAlder = alder(source.startAlder),
            grad = Uttaksgrad.from(source.grad),
            aarligInntekt = source.aarligInntektVsaPensjon?.let(::inntektSpec)
        )

    private fun inntektSpec(source: PensjonsavtaleInntektSpecV3) =
        InntektSpec(
            aarligBeloep = source.beloep,
            tomAlder = source.sluttAlder?.let(::alder) ?: alderRepresentingLivsvarig
        )

    private fun alder(source: PensjonsavtaleAlderSpecV3) =
        Alder(source.aar, source.maaneder)
}
