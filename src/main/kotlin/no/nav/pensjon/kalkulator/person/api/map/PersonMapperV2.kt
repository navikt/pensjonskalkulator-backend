package no.nav.pensjon.kalkulator.person.api.map

import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.api.dto.PersonV2
import no.nav.pensjon.kalkulator.person.api.dto.SivilstandV2
import java.time.LocalDate

object PersonMapperV2 {

    fun dtoV2(person: Person?): PersonV2 =
        person?.let {
            PersonV2(
                it.navn,
                it.foedselsdato,
                SivilstandV2.fromInternalValue(it.sivilstand)
            )
        } ?: PersonV2("", LocalDate.MIN, SivilstandV2.UOPPGITT)
}
