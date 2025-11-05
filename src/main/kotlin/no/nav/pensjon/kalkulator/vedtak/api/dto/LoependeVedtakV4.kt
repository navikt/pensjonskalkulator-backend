package no.nav.pensjon.kalkulator.vedtak.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import com.fasterxml.jackson.annotation.JsonInclude
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Sivilstand
import java.math.BigDecimal
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoependeVedtakV4(
    val harLoependeVedtak: Boolean,
    val alderspensjon: AlderspensjonDetaljerV4?,
    val fremtidigAlderspensjon: FremtidigAlderspensjonDetaljerV4?,
    val ufoeretrygd: UfoeretrygdDetaljerV4,
    val afpPrivat: LoependeFraV4?,
    val afpOffentlig: LoependeFraV4?,
    val pre2025OffentligAfp: LoependeFraV4? = null,
    val gjeldendeUttaksgradFom: LocalDate?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AlderspensjonDetaljerV4(
    val grad: Int = 0,
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    val fom: LocalDate,
    val sisteUtbetaling: UtbetalingV4? = null,
    val sivilstand: SivilstandV4,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FremtidigAlderspensjonDetaljerV4(
    val grad: Int = 0,
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    val fom: LocalDate,
)

data class UtbetalingV4(
    val beloep: BigDecimal,
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    val utbetalingsdato: LocalDate,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UfoeretrygdDetaljerV4(
    val grad: Int = 0,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoependeFraV4(
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    val fom: LocalDate,
)

enum class SivilstandV4(val internalValue: Sivilstand) {

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

        private fun default(internalValue: Sivilstand?): SivilstandV4 =
            internalValue?.let {
                log.warn { "Unknown sivilstand '$it'" }
                UNKNOWN
            } ?: UOPPGITT
    }
}
