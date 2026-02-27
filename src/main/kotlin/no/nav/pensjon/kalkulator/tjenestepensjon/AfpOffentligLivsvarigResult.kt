package no.nav.pensjon.kalkulator.tjenestepensjon

import java.time.LocalDate

data class AfpOffentligLivsvarigResult(
    val afpInnvilget: Boolean?,
    val virkningFom: LocalDate?,
    val maanedligBeloepListe: List<MaanedligBeloep>,
    val sistBenyttetGrunnbeloep: Int?
)

data class MaanedligBeloep(
    val fom: LocalDate,
    val beloep: Int,
)
