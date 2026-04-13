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
    @field:Schema(description = "Hvorvidt personen har løpende eller fremtidig vedtak")
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
    val tidsbegrensetOffentligAfpFom: LocalDate? = null,

    @field:Schema(description = "Informasjon om eventuell avdød ektefelle/partner/samboer")
    val avdoed: VedtakV1InformasjonOmAvdoed? = null
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

data class VedtakV1InformasjonOmAvdoed(
    @field:Schema(description = "Avdødes person-ID")
    val pid: String?,

    @field:Schema(description = "Dato for dødsfallet")
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    val doedsdato: LocalDate?,

    @field:Schema(description = "Virkningsdato (fra og med) for avdødes første vedtak om alderspensjon")
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    val foersteAlderspensjonVirkningsdato: LocalDate?,

    @field:Schema(description = "Hvorvidt avdødes årlige pensjonsgivende inntekt var minst 1G (grunnbeløpet)")
    val aarligPensjonsgivendeInntektErMinst1G: Boolean?,

    @field:Schema(description = "Hvorvidt avdøde hadde tilstrekkelig medlemskap i folketrygden")
    val harTilstrekkeligMedlemskapIFolketrygden: Boolean?,

    @field:Schema(description = "Antall år som avdøde hadde oppholdt seg i utlandet")
    val antallAarUtenlands: Int?,

    @field:Schema(description = "Hvorvidt avdøde var flyktning")
    val erFlyktning: Boolean?
)