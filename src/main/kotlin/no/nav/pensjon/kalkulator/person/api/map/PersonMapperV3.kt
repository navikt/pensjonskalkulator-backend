package no.nav.pensjon.kalkulator.person.api.map

import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.api.dto.PersonV3
import no.nav.pensjon.kalkulator.person.api.dto.SivilstandV3
import java.time.LocalDate

object PersonMapperV3 {

    fun dtoV3(person: Person?): PersonV3 =
        person?.let {
            PersonV3(
                it.navn,
                it.foedselsdato,
                SivilstandV3.fromInternalValue(it.sivilstand)
            )
        } ?: PersonV3("", LocalDate.MIN, SivilstandV3.UOPPGITT)
}
