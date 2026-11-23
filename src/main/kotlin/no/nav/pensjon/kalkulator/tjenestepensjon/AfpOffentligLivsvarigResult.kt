package no.nav.pensjon.kalkulator.tjenestepensjon

import java.time.LocalDate

data class AfpOffentligLivsvarigResult(
    val afpInnvilget: Boolean?,
    val virkningFom: LocalDate?,
    val maanedligBeloepListe: List<MaanedligBeloep>,
    val sistBenyttetGrunnbeloep: Int?,
    val tpNummer: String? = null,
    val tpOrdningNavn: String? = null
)

data class MaanedligBeloep(
    val fom: LocalDate,
    val beloep: Int,
)
