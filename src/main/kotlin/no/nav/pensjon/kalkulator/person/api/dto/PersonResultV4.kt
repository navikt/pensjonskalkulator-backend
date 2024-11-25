package no.nav.pensjon.kalkulator.person.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import java.time.LocalDate

data class PersonResultV4(
    val navn: String,
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd", timezone = "CET") val foedselsdato: LocalDate,
    val sivilstand: PersonSivilstandV4,
    val pensjoneringAldre: PersonPensjoneringAldreV4
)

data class PersonPensjoneringAldreV4(
    val normertPensjoneringsalder: PersonAlderV4,
    val nedreAldresgrense: PersonAlderV4
)

data class PersonAlderV4(
    val aar: Int,
    val maaneder: Int
)
