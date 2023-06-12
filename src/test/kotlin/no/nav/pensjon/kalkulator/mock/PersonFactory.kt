package no.nav.pensjon.kalkulator.mock

import no.nav.pensjon.kalkulator.person.Land
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import java.time.LocalDate

object PersonFactory {
    const val FNR = "12906498357"
    val pid = Pid(FNR)

    fun person() = Person("Fornavn1", foedselsdato(), Land.NORGE, Sivilstand.UOPPGITT)

    fun skiltPerson() = Person("Fornavn1", foedselsdato(), Land.NORGE, Sivilstand.SKILT)

    private fun foedselsdato() = LocalDate.of(1964, 10, 12)
}
