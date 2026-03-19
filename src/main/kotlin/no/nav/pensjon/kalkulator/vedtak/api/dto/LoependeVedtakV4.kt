package no.nav.pensjon.kalkulator.vedtak.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import jakarta.validation.constraints.NotNull
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Sivilstatus
import java.math.BigDecimal
import java.time.LocalDate

@JsonInclude(NON_NULL)
data class LoependeVedtakV4(
    @field:NotNull val harLoependeVedtak: Boolean,
    val alderspensjon: AlderspensjonDetaljerV4?,
    val fremtidigAlderspensjon: FremtidigAlderspensjonDetaljerV4?,
    @field:NotNull val ufoeretrygd: UfoeretrygdDetaljerV4,
    val afpPrivat: LoependeFraV4?,
    val afpOffentlig: LoependeFraV4?,
    val pre2025OffentligAfp: LoependeFraV4? = null
)

@JsonInclude(NON_NULL)
data class AlderspensjonDetaljerV4(
    @field:NotNull val grad: Int = 0,
    @field:NotNull @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val fom: LocalDate,
    @field:NotNull @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val uttaksgradFom: LocalDate,
    val sisteUtbetaling: UtbetalingV4? = null,
    @field:NotNull val sivilstand: SivilstandV4
)

data class FremtidigAlderspensjonDetaljerV4(
    @field:NotNull val grad: Int = 0,
    @field:NotNull @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val fom: LocalDate
)

data class UtbetalingV4(
    @field:NotNull val beloep: BigDecimal,
    @field:NotNull @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val utbetalingsdato: LocalDate
)

data class UfoeretrygdDetaljerV4(
    @field:NotNull val grad: Int = 0
)

data class LoependeFraV4(
    @field:NotNull @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val fom: LocalDate
)

enum class SivilstandV4(val internalValue: Sivilstatus) {

    UNKNOWN(Sivilstatus.UNKNOWN),
    UOPPGITT(Sivilstatus.UOPPGITT),
    UGIFT(Sivilstatus.UGIFT),
    GIFT(Sivilstatus.GIFT),
    ENKE_ELLER_ENKEMANN(Sivilstatus.ENKE_ELLER_ENKEMANN),
    SKILT(Sivilstatus.SKILT),
    SEPARERT(Sivilstatus.SEPARERT),
    REGISTRERT_PARTNER(Sivilstatus.REGISTRERT_PARTNER),
    SEPARERT_PARTNER(Sivilstatus.SEPARERT_PARTNER),
    SKILT_PARTNER(Sivilstatus.SKILT_PARTNER),
    GJENLEVENDE_PARTNER(Sivilstatus.GJENLEVENDE_PARTNER),
    SAMBOER(Sivilstatus.SAMBOER);

    companion object {
        private val values = entries.toTypedArray()
        private val log = KotlinLogging.logger {}

        fun fromInternalValue(value: Sivilstatus?) =
            values.singleOrNull { it.internalValue == value } ?: default(value)

        private fun default(internalValue: Sivilstatus?): SivilstandV4 =
            internalValue?.let {
                log.warn { "Unknown sivilstand '$it'" }
                UNKNOWN
            } ?: UOPPGITT
    }
}
