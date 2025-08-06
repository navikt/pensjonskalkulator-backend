package no.nav.pensjon.kalkulator.person.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import java.time.LocalDate

data class PersonResultV5(
    val navn: String,
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd", timezone = "CET") val foedselsdato: LocalDate,
    val sivilstand: PersonSivilstandV5,
    val pensjoneringAldre: PersonPensjoneringAldreV5
)

data class PersonPensjoneringAldreV5(
    val normertPensjoneringsalder: PersonAlderV5,
    val nedreAldersgrense: PersonAlderV5,
    val oevreAldersgrense: PersonAlderV5,
)

data class PersonAlderV5(
    val aar: Int,
    val maaneder: Int
)
