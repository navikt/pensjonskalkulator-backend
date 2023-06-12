package no.nav.pensjon.kalkulator.person.api.map

import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.api.dto.PersonDto

object PersonMapper {

    fun toDto(person: Person) = PersonDto(person.fornavn, person.sivilstand)
}
