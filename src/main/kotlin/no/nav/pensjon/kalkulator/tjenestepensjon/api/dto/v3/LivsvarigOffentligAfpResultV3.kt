package no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.v3

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import java.time.LocalDate

data class LivsvarigOffentligAfpResultV3(
    val afpInnvilget: Boolean?,
    val maanedligBeloepListe: List<MaanedligBeloepV3>,
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val virkningFom: LocalDate?,
    val sistBenyttetGrunnbeloep: Int?
)

data class MaanedligBeloepV3(
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val virkningFom: LocalDate?,
    val beloep: Int?,
)
