package no.nav.pensjon.kalkulator.person.client.pdl.map

import no.nav.pensjon.kalkulator.person.NavnFormatter
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.pdl.dto.PdlSivilstand
import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.client.pdl.dto.PdlAdressebeskyttelseGradering
import java.time.LocalDate

object PersonMapper {

    fun fromDto(dto: PersonResponseDto): Person? =
        dto.data?.hentPerson?.let { person(it) }

    private fun person(dto: PersonDto) =
        Person(
            fornavn = fromDto(dto.navn)?.let { NavnFormatter.formatNavn(it) },
            foedselsdato = fromDto(dto.foedsel) ?: LocalDate.MIN,
            sivilstand = fromDto(dto.sivilstand),
            adressebeskyttelse = fromDto(dto.adressebeskyttelse)
        )

    private fun fromDto(dto: List<AdressebeskyttelseDto>?): AdressebeskyttelseGradering =
        PdlAdressebeskyttelseGradering.fromExternalValue(dto?.firstOrNull()?.gradering).internalValue

    private fun fromDto(dto: List<FoedselDto>?): LocalDate? =
        dto?.firstOrNull()?.foedselsdato?.value

    private fun fromDto(dto: List<NavnDto>?): String? =
        dto?.firstOrNull()?.fornavn

    private fun fromDto(dto: List<SivilstandDto>?): Sivilstand =
        PdlSivilstand.fromExternalValue(dto?.firstOrNull()?.type).internalValue
}
