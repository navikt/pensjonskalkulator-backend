package no.nav.pensjon.kalkulator.avtale.api.map

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.api.dto.*
import no.nav.pensjon.kalkulator.general.Uttaksgrad

object PensjonsavtaleMapper {

    fun fromDto(dto: PensjonsavtaleIngressSpecDto) =
        PensjonsavtaleSpec(
            aarligInntektFoerUttak = dto.aarligInntektFoerUttak,
            uttaksperioder = dto.uttaksperioder.map(::fromUttaksperiodeSpecDto),
            antallInntektsaarEtterUttak = dto.antallInntektsaarEtterUttak,
            harEpsPensjon = dto.harEpsPensjon,
            harEpsPensjonsgivendeInntektOver2G = dto.harEpsPensjonsgivendeInntektOver2G,
            antallAarIUtlandetEtter16 = dto.antallAarIUtlandetEtter16 ?: 0,
            sivilstand = dto.sivilstand
        )

    fun toDto(source: Pensjonsavtaler) =
        PensjonsavtalerDto(
            avtaler = source.avtaler.map(::toAvtaleDto),
            utilgjengeligeSelskap = source.utilgjengeligeSelskap.map(::toSelskapDto)
        )

    private fun fromUttaksperiodeSpecDto(dto: UttaksperiodeIngressSpecDto) =
        UttaksperiodeSpec(
            startAlder = dto.startAlder,
            grad = Uttaksgrad.from(dto.grad),
            aarligInntekt = dto.aarligInntekt
        )

    private fun toAvtaleDto(source: Pensjonsavtale) =
        PensjonsavtaleDto(
            produktbetegnelse = source.produktbetegnelse,
            kategori = source.kategori,
            startAar = if (source.harStartAar) source.startAar else null,
            sluttAar = source.sluttAar,
            utbetalingsperioder = source.utbetalingsperioder.map(::toPeriodeDto)
        )

    private fun toPeriodeDto(source: Utbetalingsperiode) =
        UtbetalingsperiodeDto(
            startAlder = source.startAlder,
            sluttAlder = source.sluttAlder,
            aarligUtbetaling = source.aarligUtbetalingForventet,
            grad = source.grad.prosentsats
        )

    private fun toSelskapDto(source: Selskap) =
        SelskapDto(
            navn = source.navn,
            heltUtilgjengelig = source.heltUtilgjengelig
        )
}
