package no.nav.pensjon.kalkulator.mock

import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import java.time.LocalDate

object PersonFactory {
    val pid = Pid("12906498357") // synthetic fødselsnummer
    val foedselsdato = LocalDate.of(1963, 12, 31)

    fun person(sivilstand: Sivilstand) = Person("Fornavn1", foedselsdato, sivilstand)

    fun person() = person(Sivilstand.UOPPGITT)

    fun skiltPerson() = Person("Fornavn1", foedselsdato, Sivilstand.SKILT)
}
