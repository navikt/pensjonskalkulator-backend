package no.nav.pensjon.kalkulator.person.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import java.time.LocalDate

data class PersonResultV6(
    val navn: String,
    val fornavn: String,
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val foedselsdato: LocalDate,
    val sivilstand: PersonSivilstandV6,
    val pensjoneringAldre: PersonPensjonsaldreV6
)

data class PersonPensjonsaldreV6(
    val normertPensjoneringsalder: PersonAlderV6,
    val nedreAldersgrense: PersonAlderV6,
    val oevreAldersgrense: PersonAlderV6
)

data class PersonAlderV6(
    val aar: Int,
    val maaneder: Int
)
