package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.simulering.InnvilgetLivsvarigOffentligAfpSpec
import java.time.LocalDate

/**
 * Spesifiserer egenskapene til en løpende livsvarig AFP i offentlig sektor.
 */
data class SimuleringV2InnvilgetLivsvarigOffentligAfpSpec(
    @field:Schema(description = "Årlig bruttobeløp (norske kroner)")
    @field:NotNull
    val aarligBruttoBeloep: Double,

    @field:Schema(description = "Dato (fra og med) ved start av uttak av livsvarig AFP i offentlig sektor")
    @field:NotNull
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    val uttakFom: LocalDate,

    @field:Schema(description = "Sist regulert grunnbeløp (norske kroner)")
    val sistRegulertGrunnbeloep: Int? = null
) {
    fun toInternalValue() =
        InnvilgetLivsvarigOffentligAfpSpec(
            aarligBruttoBeloep = aarligBruttoBeloep,
            uttakFom = uttakFom,
            sistRegulertGrunnbeloep = sistRegulertGrunnbeloep
        )
}