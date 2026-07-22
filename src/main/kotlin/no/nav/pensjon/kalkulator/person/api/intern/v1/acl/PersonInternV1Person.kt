package no.nav.pensjon.kalkulator.person.api.intern.v1.acl

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

/**
 * Transferable representation (data transfer object) of 'person'.
 * Used in version 1 of the 'person' service for 'intern kalkulator'.
 * -----
 * Using the prefix 'PersonInternV1' to avoid name clash with other DTOs (which causes problems in the generated
 * Swagger API documentation).
 * An alternative is to use 'springdoc.use-fqn=true', but this causes problems for the frontend's type checker (which
 * cannot handle DTO names with dots).
 */
data class PersonInternV1Person(
    @field:Schema(description = "Personens navn, format: Fornavn (Mellomnavn) Etternavn")
    @field:NotNull
    val navn: String,

    @field:Schema(description = "Personens fødselsdato")
    @field:NotNull
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    val foedselsdato: LocalDate,

    @field:Schema(description = "Personens sivilstand")
    @field:NotNull
    val sivilstand: PersonInternV1Sivilstand,

    @field:Schema(description = "Personens sivilstatus (sivilstand pluss samboerskap)")
    @field:NotNull
    val sivilstatus: PersonInternV1Sivilstatus,

    @field:Schema(description = "Personens mulige pensjoneringsaldre")
    @field:NotNull
    val pensjoneringAldre: PersonInternV1Pensjonsaldre
)

data class PersonInternV1Pensjonsaldre(
    @field:Schema(description = "Normert pensjonsalder (alder for ubetinget start av pensjonsuttak)")
    @field:NotNull
    val normertPensjoneringsalder: PersonInternV1Alder,

    @field:Schema(description = "Nedre aldersgrense for start av pensjonsuttak")
    @field:NotNull
    val nedreAldersgrense: PersonInternV1Alder,

    @field:Schema(description = "Øvre aldersgrense for start av pensjonsuttak")
    @field:NotNull
    val oevreAldersgrense: PersonInternV1Alder
)

data class PersonInternV1Alder(
    @field:Schema(description = "Antall fylte år")
    @field:NotNull
    val aar: Int,

    @field:Schema(description = "Antall fylte måneder (0-11)")
    @field:NotNull
    val maaneder: Int
)