package no.nav.pensjon.kalkulator.person.client.pdl.dto

import java.time.LocalDate

data class PersonResponseDto(val data: PersonEnvelopeDto?, val errors: List<ErrorDto>?)

data class PersonEnvelopeDto(val hentPerson: PersonDto)

data class PersonDto(
    val navn: List<NavnDto>,
    val foedsel: List<FoedselDto>,
    val statsborgerskap: List<StatsborgerskapDto>,
    val sivilstand: List<SivilstandDto>
)

data class NavnDto(val fornavn: String)

data class FoedselDto(val foedselsdato: LocalDate)

data class StatsborgerskapDto(val land: String)

data class SivilstandDto(val type: String)

data class ErrorDto(val message: String)
