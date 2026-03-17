package no.nav.pensjon.kalkulator.vedtak.api.v1.acl

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.Sivilstatus
import java.math.BigDecimal
import java.time.LocalDate

@JsonInclude(NON_NULL)
data class VedtakV1Samling(
    @field:Schema(description = "Om personen har løpende vedtak")
    @field:NotNull val harVedtak: Boolean,

    @field:Schema(description = "Løpende alderspensjon")
    val loependeAlderspensjon: VedtakV1LoependeAlderspensjon?,

    @field:Schema(description = "Fremtidig alderspensjon")
    val fremtidigAlderspensjon: VedtakV1Alderspensjonsuttak?,

    @field:Schema(description = "Grad av uføretrygd (0-100)")
    val ufoeretrygdgrad: Int?,

    @field:Schema(description = "Startdato (fra og med) for AFP i privat sektor")
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    val privatAfpFom: LocalDate?,

    @field:Schema(description = "Startdato (fra og med) for tidsbegrenset AFP i offentlig sektor ('gammel ordning')")
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    val tidsbegrensetOffentligAfpFom: LocalDate? = null
)

@JsonInclude(NON_NULL)
data class VedtakV1LoependeAlderspensjon(
    @field:Schema(description = "Uttaksgrad (prosent) for alderspensjonen")
    @field:NotNull
    val grad: Int = 0,

    @field:Schema(description = "Startdato (fra og med) for alderspensjonen")
    @field:NotNull
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    val fom: LocalDate,

    @field:Schema(description = "Startdato (fra og med) for uttaksgraden")
    @field:NotNull
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    val uttaksgradFom: LocalDate,

    @field:Schema(description = "Siste utbetaling av alderspensjon")
    val sisteUtbetaling: VedtakV1Utbetaling? = null,

    @field:Schema(description = "Sivilstatus")
    @field:NotNull
    val sivilstatus: VedtakV1Sivilstatus
)

data class VedtakV1Alderspensjonsuttak(
    @field:Schema(description = "Uttaksgrad (prosent) for alderspensjonen")
    @field:NotNull
    val grad: Int = 0,

    @field:Schema(description = "Startdato (fra og med) for alderspensjonen")
    @field:NotNull
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val fom: LocalDate
)

data class VedtakV1Utbetaling(
    @field:Schema(description = "Beløp utbetalt")
    @field:NotNull
    val beloep: BigDecimal,

    @field:Schema(description = "Dato for utbetalingen")
    @field:NotNull @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    val utbetalingsdato: LocalDate
)

enum class VedtakV1Sivilstatus(val internalValue: Sivilstatus) {

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
        private val log = KotlinLogging.logger {}

        fun fromInternalValue(value: Sivilstatus?) =
            entries.singleOrNull { it.internalValue == value } ?: default(value)

        private fun default(internalValue: Sivilstatus?): VedtakV1Sivilstatus =
            internalValue?.let {
                log.warn { "Unknown sivilstatus '$it'" }
                UNKNOWN
            } ?: UOPPGITT
    }
}
