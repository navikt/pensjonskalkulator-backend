package no.nav.pensjon.kalkulator.avtale.api.map

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.api.dto.*
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad

object PensjonsavtaleMapper {

    fun fromDto(dto: PensjonsavtaleIngressSpecDto) =
        PensjonsavtaleSpec(
            aarligInntektFoerUttak = dto.aarligInntektFoerUttak,
            uttaksperioder = dto.uttaksperioder.map(::fromUttaksperiodeSpecDto),
            antallInntektsaarEtterUttak = dto.antallInntektsaarEtterUttak,
            harAfp = dto.harAfp ?: false,
            harEpsPensjon = dto.harEpsPensjon,
            harEpsPensjonsgivendeInntektOver2G = dto.harEpsPensjonsgivendeInntektOver2G,
            antallAarIUtlandetEtter16 = dto.antallAarIUtlandetEtter16 ?: 0,
            sivilstand = dto.sivilstand
        )

    fun fromDto(dto: PensjonsavtaleIngressSpecV0Dto) =
        PensjonsavtaleSpec(
            aarligInntektFoerUttak = dto.aarligInntektFoerUttak,
            uttaksperioder = dto.uttaksperioder.map(::fromUttaksperiodeSpecV0Dto),
            antallInntektsaarEtterUttak = dto.antallInntektsaarEtterUttak,
            harAfp = dto.harAfp ?: false,
            harEpsPensjon = dto.harEpsPensjon,
            harEpsPensjonsgivendeInntektOver2G = dto.harEpsPensjonsgivendeInntektOver2G,
            antallAarIUtlandetEtter16 = dto.antallAarIUtlandetEtter16 ?: 0,
            sivilstand = dto.sivilstatus
        )

    fun toDto(source: Pensjonsavtaler) =
        PensjonsavtalerDto(
            avtaler = source.avtaler.map(::toAvtaleDto),
            utilgjengeligeSelskap = source.utilgjengeligeSelskap.map(::toSelskapDto)
        )

    fun toV0Dto(source: Pensjonsavtaler) =
        PensjonsavtalerV0Dto(
            avtaler = source.avtaler.map(::toAvtaleV0Dto),
            utilgjengeligeSelskap = source.utilgjengeligeSelskap.map(::toSelskapDto)
        )

    private fun fromUttaksperiodeSpecDto(dto: UttaksperiodeIngressSpecDto) =
        UttaksperiodeSpec(
            start = dto.start,
            grad = Uttaksgrad.from(dto.grad),
            aarligInntekt = dto.aarligInntekt
        )

    private fun fromUttaksperiodeSpecV0Dto(dto: UttaksperiodeIngressSpecV0Dto) =
        UttaksperiodeSpec(
            start = Alder(dto.startAlder, dto.startMaaned - 1), // input is 1..12, we use 0..11 => minus 1
            grad = Uttaksgrad.from(dto.grad),
            aarligInntekt = dto.aarligInntekt
        )

    private fun toAvtaleDto(source: Pensjonsavtale) =
        PensjonsavtaleDto(
            produktbetegnelse = source.produktbetegnelse,
            kategori = source.kategori,
            startAlder = if (source.harStartalder) source.startalder else null,
            sluttAlder = source.sluttalder,
            utbetalingsperioder = source.utbetalingsperioder.map(::toPeriodeDto)
        )

    private fun toAvtaleV0Dto(source: Pensjonsavtale) =
        PensjonsavtaleV0Dto(
            produktbetegnelse = source.produktbetegnelse,
            kategori = source.kategori,
            startAlder = if (source.harStartalder) source.startalder else null,
            sluttAlder = source.sluttalder,
            utbetalingsperioder = source.utbetalingsperioder.map(::toPeriodeV0Dto)
        )

    private fun toPeriodeDto(source: Utbetalingsperiode) =
        UtbetalingsperiodeDto(
            start = source.start,
            slutt = source.slutt,
            aarligUtbetaling = source.aarligUtbetalingForventet,
            grad = source.grad.prosentsats
        )

    private fun toPeriodeV0Dto(source: Utbetalingsperiode) =
        UtbetalingsperiodeV0Dto(
            startAlder = source.start.aar,
            startMaaned = source.start.maaneder + 1,
            sluttAlder = source.slutt?.aar,
            sluttMaaned = source.slutt?.let { it.maaneder + 1 },
            aarligUtbetaling = source.aarligUtbetalingForventet,
            grad = source.grad.prosentsats
        )

    private fun toSelskapDto(source: Selskap) =
        SelskapDto(
            navn = source.navn,
            heltUtilgjengelig = source.heltUtilgjengelig
        )
}
