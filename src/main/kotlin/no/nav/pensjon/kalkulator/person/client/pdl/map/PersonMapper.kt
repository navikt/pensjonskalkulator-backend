package no.nav.pensjon.kalkulator.person.client.pdl.map

import no.nav.pensjon.kalkulator.person.NavnFormatter
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import java.time.LocalDate

object PersonMapper {

    fun fromDto(dto: PersonResponseDto): Person? =
        dto.data?.hentPerson?.let { person(it) }

    private fun person(dto: PersonDto) =
        Person(
            fornavn = fromDto(dto.navn)?.let { NavnFormatter.formatNavn(it) },
            foedselsdato = fromDto(dto.foedsel) ?: LocalDate.MIN,
            sivilstand = fromDto(dto.sivilstand)
        )

    private fun fromDto(dto: List<FoedselDto>?) =
        dto?.firstOrNull()?.foedselsdato?.value

    private fun fromDto(dto: List<NavnDto>?) =
        dto?.firstOrNull()?.fornavn

    private fun fromDto(dto: List<SivilstandDto>?) =
        dto?.firstOrNull()?.type?.let { Sivilstand.forPdlCode(it) }
}
