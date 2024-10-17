package no.nav.pensjon.kalkulator.vedtak.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoependeVedtakV2(
    val alderspensjon: AlderspensjonDetaljerV2?,
    val harFremtidigLoependeVedtak: Boolean = false,
    val ufoeretrygd: UfoeretrygdDetaljerV2,
    val afpPrivat: LoependeFraV2?,
    val afpOffentlig: LoependeFraV2?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AlderspensjonDetaljerV2(
    val grad: Int = 0,
    val fom: LocalDate,
    val sisteUtbetaling: UtbetalingV2? = null,
)

data class UtbetalingV2(
    val beloep: BigDecimal,
    val utbetalingsdato: LocalDate,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UfoeretrygdDetaljerV2(
    val grad: Int = 0,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoependeFraV2(
    val fom: LocalDate,
)
