package no.nav.pensjon.kalkulator.mock

import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand

object PersonFactory {
    val pid = Pid("12906498357")

    fun person() = Person("Fornavn1", Sivilstand.UOPPGITT)

    fun skiltPerson() = Person("Fornavn1", Sivilstand.SKILT)

}
