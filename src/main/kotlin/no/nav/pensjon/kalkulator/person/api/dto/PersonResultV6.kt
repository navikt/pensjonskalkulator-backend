package no.nav.pensjon.kalkulator.person.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class PersonResultV6(
    @field:NotNull val navn: String,
    @field:NotNull val fornavn: String,
    @field:NotNull @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val foedselsdato: LocalDate,
    @field:NotNull val sivilstand: PersonSivilstandV6, //TODO: not used in frontend?
    @field:NotNull val pensjoneringAldre: PersonPensjonsaldreV6
)

data class PersonPensjonsaldreV6(
    @field:NotNull val normertPensjoneringsalder: PersonAlderV6,
    @field:NotNull val nedreAldersgrense: PersonAlderV6,
    @field:NotNull val oevreAldersgrense: PersonAlderV6
)

data class PersonAlderV6(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
)
