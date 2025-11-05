package no.nav.pensjon.kalkulator.vedtak.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Sivilstand
import java.math.BigDecimal
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoependeVedtakV3(
    val alderspensjon: AlderspensjonDetaljerV3?,
    val harFremtidigLoependeVedtak: Boolean = false,
    val ufoeretrygd: UfoeretrygdDetaljerV3,
    val afpPrivat: LoependeFraV3?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AlderspensjonDetaljerV3(
    val grad: Int = 0,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val fom: LocalDate,
    val sisteUtbetaling: UtbetalingV3? = null,
    val sivilstand: SivilstandV3,
)

data class UtbetalingV3(
    val beloep: BigDecimal,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val utbetalingsdato: LocalDate,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UfoeretrygdDetaljerV3(
    val grad: Int = 0,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoependeFraV3(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val fom: LocalDate,
)

enum class SivilstandV3(val internalValue: Sivilstand) {

    UNKNOWN(Sivilstand.UNKNOWN),
    UOPPGITT(Sivilstand.UOPPGITT),
    UGIFT(Sivilstand.UGIFT),
    GIFT(Sivilstand.GIFT),
    ENKE_ELLER_ENKEMANN(Sivilstand.ENKE_ELLER_ENKEMANN),
    SKILT(Sivilstand.SKILT),
    SEPARERT(Sivilstand.SEPARERT),
    REGISTRERT_PARTNER(Sivilstand.REGISTRERT_PARTNER),
    SEPARERT_PARTNER(Sivilstand.SEPARERT_PARTNER),
    SKILT_PARTNER(Sivilstand.SKILT_PARTNER),
    GJENLEVENDE_PARTNER(Sivilstand.GJENLEVENDE_PARTNER),
    SAMBOER(Sivilstand.SAMBOER);

    companion object {
        private val values = entries.toTypedArray()
        private val log = KotlinLogging.logger {}

        fun fromInternalValue(value: Sivilstand?) =
            values.singleOrNull { it.internalValue == value } ?: default(value)

        private fun default(internalValue: Sivilstand?): SivilstandV3 =
            internalValue?.let {
                log.warn { "Unknown sivilstand '$it'" }
                UNKNOWN
            } ?: UOPPGITT
    }
}
