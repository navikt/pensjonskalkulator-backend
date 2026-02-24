package no.nav.pensjon.kalkulator.person.api.intern.v1.acl

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class PersonDto(
    @field:NotNull val navn: String,
    @field:NotNull @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val foedselsdato: LocalDate,
    @field:NotNull val sivilstatus: SivilstatusDto,
    @field:NotNull val pensjoneringAldre: PensjonsaldreDto
)

data class PensjonsaldreDto(
    @field:NotNull val normertPensjoneringsalder: AlderDto,
    @field:NotNull val nedreAldersgrense: AlderDto,
    @field:NotNull val oevreAldersgrense: AlderDto
)

data class AlderDto(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
)
