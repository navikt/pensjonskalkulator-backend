package no.nav.pensjon.kalkulator.person.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService.Companion.defaultAldersgrenser
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.api.dto.PersonAlderV5
import no.nav.pensjon.kalkulator.person.api.dto.PersonPensjoneringAldreV5
import no.nav.pensjon.kalkulator.person.api.dto.PersonResultV5
import no.nav.pensjon.kalkulator.person.api.dto.PersonSivilstandV5
import java.time.LocalDate

object PersonMapperV5 {

    fun dtoV5(source: Person?): PersonResultV5 =
        source?.let(::person) ?: defaultPerson()

    private fun person(source: Person) =
        PersonResultV5(
            navn = source.fornavn,
            foedselsdato = source.foedselsdato,
            sivilstand = PersonSivilstandV5.fromInternalValue(source.sivilstand),
            pensjoneringAldre = source.pensjoneringAldre.let(::pensjoneringAldre)
        )

    private fun defaultPerson() =
        PersonResultV5(
            navn = "",
            foedselsdato = LocalDate.MIN,
            sivilstand = PersonSivilstandV5.UOPPGITT,
            pensjoneringAldre = defaultAldersgrenser.let(::pensjoneringAldre)
        )

    private fun pensjoneringAldre(source: Aldersgrenser) =
        PersonPensjoneringAldreV5(
            normertPensjoneringsalder = alder(source.normalder),
            nedreAldersgrense = alder(source.nedreAlder),
            oevreAldersgrense = alder(source.oevreAlder),
        )

    private fun alder(source: Alder) =
        PersonAlderV5(source.aar, source.maaneder)
}
