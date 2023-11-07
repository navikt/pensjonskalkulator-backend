package no.nav.pensjon.kalkulator.person.api.map

import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.api.dto.ApiPersonDto
import no.nav.pensjon.kalkulator.person.api.dto.ApiSivilstand
import no.nav.pensjon.kalkulator.person.api.dto.PersonDto
import java.time.LocalDate

object PersonMapper {

    fun toDto(person: Person?): ApiPersonDto =
        person?.let {
            ApiPersonDto(
                it.fornavn,
                it.foedselsdato,
                ApiSivilstand.fromInternalValue(it.sivilstand)
            )
        } ?: ApiPersonDto("", LocalDate.MIN, ApiSivilstand.UOPPGITT)

    fun toV0Dto(person: Person?) =
        PersonDto(
            person?.fornavn,
            person?.foedselsdato,
            person?.sivilstand
        )
}
