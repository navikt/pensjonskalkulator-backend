package no.nav.pensjon.kalkulator.person.client.pdl.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.time.LocalDate

data class PersonResponseDto(
    val data: PersonEnvelopeDto?,
    val extensions: ExtensionsDto?,
    val errors: List<ErrorDto>?
)

data class PersonEnvelopeDto(val hentPerson: PersonDto?) // null if person not found

data class PersonDto(
    val navn: List<NavnDto>?,
    val foedsel: List<FoedselDto>?,
    val sivilstand: List<SivilstandDto>?,
    val adressebeskyttelse: List<AdressebeskyttelseDto>?
)

data class NavnDto(
    val fornavn: String? = null,
    val mellomnavn: String? = null,
    val etternavn: String? = null
)

data class FoedselDto(val foedselsdato: DateDto)

data class SivilstandDto(val type: String)

data class AdressebeskyttelseDto(val gradering: String)

data class ErrorDto(val message: String)

data class ExtensionsDto(val warnings: List<WarningDto>?)

data class WarningDto(
    val query: String?,
    val id: String?,
    val code: String?,
    val message: String?,
    val details: Any?
)

data class DateDto(val value: LocalDate) {
    @JsonValue
    fun rawValue() = converter.toJson(value)

    companion object {
        val converter = DateScalarConverter()

        @JsonCreator
        @JvmStatic
        fun create(rawValue: String) = DateDto(converter.toScalar(rawValue))
    }
}
