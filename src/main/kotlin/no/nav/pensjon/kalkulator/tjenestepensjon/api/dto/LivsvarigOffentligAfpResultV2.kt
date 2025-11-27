package no.nav.pensjon.kalkulator.tjenestepensjon.api.dto

import java.time.LocalDate

/**
 * Dataoverføringsobjekt (DTO) for innvilget livsvarig offentlig AFP i offentlig sektor.
 */
data class LivsvarigOffentligAfpResultV2(
    val afpStatus: Boolean?,
    val virkningFom: LocalDate?,
    val maanedligBeloep: Int?,
    val sistBenyttetGrunnbeloep: Int?
)
