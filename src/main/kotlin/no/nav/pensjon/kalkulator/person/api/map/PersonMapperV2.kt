package no.nav.pensjon.kalkulator.person.api.map

import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.api.dto.ApiSivilstand
import no.nav.pensjon.kalkulator.person.api.dto.PersonV2
import java.time.LocalDate

object PersonMapperV2 {

    fun dtoV2(person: Person?): PersonV2 =
        person?.let {
            PersonV2(
                it.navn,
                it.foedselsdato,
                ApiSivilstand.fromInternalValue(it.sivilstand)
            )
        } ?: PersonV2("", LocalDate.MIN, ApiSivilstand.UOPPGITT)
}
