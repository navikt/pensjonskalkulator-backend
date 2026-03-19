package no.nav.pensjon.kalkulator.vedtak.api.v1.acl

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.common.api.acl.CommonV1Sivilstatus
import java.math.BigDecimal
import java.time.LocalDate

@JsonInclude(NON_NULL)
data class VedtakV1Samling(
    @field:Schema(description = "Om personen har løpende eller fremtidig vedtak")
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
    val sivilstatus: CommonV1Sivilstatus
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
