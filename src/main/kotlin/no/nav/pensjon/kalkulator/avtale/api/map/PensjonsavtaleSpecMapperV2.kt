package no.nav.pensjon.kalkulator.avtale.api.map

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.api.dto.*
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import java.time.LocalDate

object PensjonsavtaleSpecMapperV2 {

    private const val DAGER_PER_AAR = 365
    private val alderRepresentingLivsvarig = Alder(aar = 99, maaneder = 11)

    fun fromDtoV2(source: PensjonsavtaleSpecV2) =
        PensjonsavtaleSpec(
            aarligInntektFoerUttak = source.aarligInntektFoerUttakBeloep,
            uttaksperioder = source.uttaksperioder.map(this::uttaksperiodeSpec),
            harEpsPensjon = source.harEpsPensjon,
            harEpsPensjonsgivendeInntektOver2G = source.harEpsPensjonsgivendeInntektOver2G,
            antallAarIUtlandetEtter16 = source.antallAarIUtlandetEtter16 ?: antallAar(source.utenlandsperioder.orEmpty()),
            sivilstand = source.sivilstand?.internalValue
        )

    private fun antallAar(oppholdListe: List<PensjonsavtaleOppholdSpecV2>) =
        (antallDager(oppholdListe) / DAGER_PER_AAR).toInt()

    private fun antallDager(oppholdListe: List<PensjonsavtaleOppholdSpecV2>) =
        oppholdListe.sumOf { (it.tom ?: LocalDate.now()).toEpochDay() - it.fom.toEpochDay() }

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
