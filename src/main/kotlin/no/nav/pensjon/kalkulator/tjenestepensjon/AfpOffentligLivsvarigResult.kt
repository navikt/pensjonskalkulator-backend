package no.nav.pensjon.kalkulator.tjenestepensjon

import java.time.LocalDate

data class AfpOffentligLivsvarigResult(
    val afpStatus: Boolean?,
    val virkningFom: LocalDate?,
    val maanedligBeloep: Int?,
    val sistBenyttetGrunnbeloep: Int?
)
