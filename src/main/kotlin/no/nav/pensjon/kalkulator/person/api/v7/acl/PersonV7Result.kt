package no.nav.pensjon.kalkulator.person.api.v7.acl

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

/**
 * Using the prefix 'PersonV7' to avoid name clash with other DTOs (which causes problems in the generated Swagger API
 * documentation).
 * An alternative is to use 'springdoc.use-fqn=true', but this causes problems for the frontend's type checker (which
 * cannot handle DTO names with dots).
 */
data class PersonV7Result(
    @field:NotNull val navn: String,
    @field:NotNull val fornavn: String,
    @field:NotNull @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val foedselsdato: LocalDate,
    @field:NotNull val sivilstatus: PersonV7Sivilstatus,
    @field:NotNull val pensjoneringAldre: PersonV7Pensjonsaldre
)

data class PersonV7Pensjonsaldre(
    @field:NotNull val normertPensjoneringsalder: PersonV7Alder,
    @field:NotNull val nedreAldersgrense: PersonV7Alder,
    @field:NotNull val oevreAldersgrense: PersonV7Alder
)

data class PersonV7Alder(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
)
