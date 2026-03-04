package no.nav.pensjon.kalkulator.person.api.intern.v1.acl

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

/**
 * Using the prefix 'PersonInternV1' to avoid name clash with other DTOs (which causes problems in the generated
 * Swagger API documentation).
 * An alternative is to use 'springdoc.use-fqn=true', but this causes problems for the frontend's type checker (which
 * cannot handle DTO names with dots).
 */
data class PersonInternV1Person(
    @field:NotNull val navn: String,
    @field:NotNull @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val foedselsdato: LocalDate,
    @field:NotNull val sivilstatus: PersonInternV1Sivilstatus,
    @field:NotNull val pensjoneringAldre: PersonInternV1Pensjonsaldre
)

data class PersonInternV1Pensjonsaldre(
    @field:NotNull val normertPensjoneringsalder: PersonInternV1Alder,
    @field:NotNull val nedreAldersgrense: PersonInternV1Alder,
    @field:NotNull val oevreAldersgrense: PersonInternV1Alder
)

data class PersonInternV1Alder(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
)
