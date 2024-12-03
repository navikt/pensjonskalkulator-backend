package no.nav.pensjon.kalkulator.avtale.api.map

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.api.dto.*
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad

object PensjonsavtaleSpecMapperV2 {

    private val alderRepresentingLivsvarig = Alder(aar = 99, maaneder = 11)

    fun fromDtoV2(source: PensjonsavtaleSpecV2) =
        PensjonsavtaleSpec(
            aarligInntektFoerUttak = source.aarligInntektFoerUttakBeloep,
            uttaksperioder = source.uttaksperioder.map(this::uttaksperiodeSpec),
            harEpsPensjon = source.harEpsPensjon,
            harEpsPensjonsgivendeInntektOver2G = source.harEpsPensjonsgivendeInntektOver2G,
            sivilstand = source.sivilstand?.internalValue
        )

    private fun uttaksperiodeSpec(source: PensjonsavtaleUttaksperiodeSpecV2) =
        UttaksperiodeSpec(
            startAlder = alder(source.startAlder),
            grad = Uttaksgrad.from(source.grad),
            aarligInntekt = source.aarligInntektVsaPensjon?.let(::inntektSpec)
        )

    private fun inntektSpec(source: PensjonsavtaleInntektSpecV2) =
        InntektSpec(
            aarligBeloep = source.beloep,
            tomAlder = source.sluttAlder?.let(::alder) ?: alderRepresentingLivsvarig
        )

    private fun alder(source: PensjonsavtaleAlderSpecV2) = Alder(source.aar, source.maaneder)
}
