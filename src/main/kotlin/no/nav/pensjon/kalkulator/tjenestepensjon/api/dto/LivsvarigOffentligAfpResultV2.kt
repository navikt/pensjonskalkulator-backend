package no.nav.pensjon.kalkulator.tjenestepensjon.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import java.time.LocalDate

/**
 * Dataoverføringsobjekt (DTO) for innvilget livsvarig offentlig AFP i offentlig sektor.
 */
@JsonInclude(NON_NULL)
data class LivsvarigOffentligAfpResultV2(
    val afpStatus: Boolean?,
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val virkningFom: LocalDate?,
    val maanedligBeloep: Int?,
    val sistBenyttetGrunnbeloep: Int?
)
