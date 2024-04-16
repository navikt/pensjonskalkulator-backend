package no.nav.pensjon.kalkulator.person.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class PersonV2(
    val navn: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val foedselsdato: LocalDate,
    val sivilstand: ApiSivilstand
)
