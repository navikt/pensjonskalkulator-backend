package no.nav.pensjon.kalkulator.avtale.api.map

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.api.dto.*
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad

object PensjonsavtaleMapper {

    private val alderRepresentingLivsvarig = Alder(aar = 99, maaneder = 11)

    // V1
    fun fromDto(dto: PensjonsavtaleIngressSpecDto) =
        PensjonsavtaleSpec(
            aarligInntektFoerUttak = dto.aarligInntektFoerUttak,
            uttaksperioder = dto.uttaksperioder.map(::uttaksperiodeSpec),
            antallInntektsaarEtterUttak = dto.antallInntektsaarEtterUttak, // V1 only
            harEpsPensjon = dto.harEpsPensjon,
            harEpsPensjonsgivendeInntektOver2G = dto.harEpsPensjonsgivendeInntektOver2G,
            antallAarIUtlandetEtter16 = dto.antallAarIUtlandetEtter16 ?: 0,
            sivilstand = dto.sivilstand
        )

    fun fromDtoV2(dto: IngressPensjonsavtaleSpecV2) =
        PensjonsavtaleSpec(
            aarligInntektFoerUttak = dto.aarligInntektFoerUttakBeloep,
            uttaksperioder = dto.uttaksperioder.map(this::uttaksperiodeSpec),
            harEpsPensjon = dto.harEpsPensjon,
            harEpsPensjonsgivendeInntektOver2G = dto.harEpsPensjonsgivendeInntektOver2G,
            antallAarIUtlandetEtter16 = dto.antallAarIUtlandetEtter16 ?: 0,
            sivilstand = dto.sivilstand
        )

    fun toDto(source: Pensjonsavtaler) =
        PensjonsavtalerDto(
            avtaler = source.avtaler.map(::avtaleDto),
            utilgjengeligeSelskap = source.utilgjengeligeSelskap.map(::selskapDto)
        )

    // V1
    private fun uttaksperiodeSpec(dto: UttaksperiodeIngressSpecDto) =
        UttaksperiodeSpec(
            startAlder = dto.startAlder,
            grad = Uttaksgrad.from(dto.grad),
            aarligInntekt = InntektSpec(aarligBeloep = dto.aarligInntekt, tomAlder = null)
            // tomAlder not used in V1 (antallInntektsaarEtterUttak is used instead)
        )

    private fun uttaksperiodeSpec(dto: IngressPensjonsavtaleUttaksperiodeV2) =
        UttaksperiodeSpec(
            startAlder = dto.startAlder,
            grad = Uttaksgrad.from(dto.grad),
            aarligInntekt = dto.aarligInntektVsaPensjon?.let(::inntektSpec)
        )

    private fun inntektSpec(dto: IngressPensjonsavtaleInntektV2) =
        InntektSpec(
            aarligBeloep = dto.beloep,
            tomAlder = dto.sluttAlder?.let(::alder) ?: alderRepresentingLivsvarig
        )

    private fun alder(dto: IngressPensjonsavtaleAlderV2) = Alder(dto.aar, dto.maaneder)

    private fun avtaleDto(source: Pensjonsavtale) =
        PensjonsavtaleDto(
            produktbetegnelse = source.produktbetegnelse,
            kategori = source.kategori,
            startAar = source.startAar,
            sluttAar = source.sluttAar,
            utbetalingsperioder = source.utbetalingsperioder.map(::periodeDto)
        )

    private fun periodeDto(source: Utbetalingsperiode) =
        UtbetalingsperiodeDto(
            startAlder = source.startAlder,
            sluttAlder = source.sluttAlder,
            aarligUtbetaling = source.aarligUtbetalingForventet,
            grad = source.grad.prosentsats
        )

    private fun selskapDto(source: Selskap) =
        SelskapDto(
            navn = source.navn,
            heltUtilgjengelig = source.heltUtilgjengelig
        )
}
