package no.nav.pensjon.kalkulator.person.client.pdl.map

import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.NavnFormatter.formatNavn
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import java.time.LocalDate

object PersonMapper {

    fun fromDto(dto: PersonResponseDto): Person? = dto.data?.hentPerson?.let(::person)

    private fun person(dto: PersonDto) =
        Person(
            navn = dto.navn.orEmpty().let(::fromDto) ?: "",
            foedselsdato = dto.foedsel.orEmpty().let(::fromDto) ?: LocalDate.MIN,
            sivilstand = dto.sivilstand.orEmpty().let(::fromDto),
            adressebeskyttelse = dto.adressebeskyttelse.orEmpty().let(::fromDto)
        )

    private fun fromDto(dto: List<AdressebeskyttelseDto>): AdressebeskyttelseGradering =
        PdlAdressebeskyttelseGradering.fromExternalValue(dto.firstOrNull()?.gradering).internalValue

    private fun fromDto(dto: List<FoedselDto>): LocalDate? = dto.firstOrNull()?.foedselsdato?.value

    private fun fromDto(dto: List<NavnDto>): String? =
        dto.firstOrNull()?.let { formatNavn(it.fornavn, it.mellomnavn, it.etternavn) }

    private fun fromDto(dto: List<SivilstandDto>): Sivilstand =
        PdlSivilstand.fromExternalValue(dto.firstOrNull()?.type).internalValue
}
