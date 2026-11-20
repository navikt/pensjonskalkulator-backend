package no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.v4

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import java.time.LocalDate

data class LivsvarigOffentligAfpResultV4(
    val afpInnvilget: Boolean?,
    val maanedligBeloepListe: List<MaanedligBeloepV4>,
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val virkningFom: LocalDate?,
    val sistBenyttetGrunnbeloep: Int?,
    val tpNummer: String?,
    val tpOrdningNavn: String?
)

data class MaanedligBeloepV4(
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val virkningFom: LocalDate?,
    val beloep: Int?,
)
