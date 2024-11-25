package no.nav.pensjon.kalkulator.person.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.api.dto.PersonAlderV4
import no.nav.pensjon.kalkulator.person.api.dto.PersonPensjoneringAldreV4
import no.nav.pensjon.kalkulator.person.api.dto.PersonResultV4
import no.nav.pensjon.kalkulator.person.api.dto.PersonSivilstandV4
import no.nav.pensjon.kalkulator.uttaksalder.normalder.NormertPensjoneringsalderService
import no.nav.pensjon.kalkulator.uttaksalder.normalder.PensjoneringAldre
import java.time.LocalDate

object PersonMapperV4 {

    fun dtoV4(source: Person?): PersonResultV4 =
        source?.let(::person) ?: defaultPerson()

    private fun person(source: Person) =
        PersonResultV4(
            navn = source.navn,
            foedselsdato = source.foedselsdato,
            sivilstand = PersonSivilstandV4.fromInternalValue(source.sivilstand),
            pensjoneringAldre = source.pensjoneringAldre.let(::pensjoneringAldre)
        )

    private fun defaultPerson() =
        PersonResultV4(
            navn = "",
            foedselsdato = LocalDate.MIN,
            sivilstand = PersonSivilstandV4.UOPPGITT,
            pensjoneringAldre = NormertPensjoneringsalderService.defaultAldre.let(::pensjoneringAldre)
        )

    private fun pensjoneringAldre(source: PensjoneringAldre) =
        PersonPensjoneringAldreV4(
            normertPensjoneringsalder = alder(source.normalder),
            nedreAldersgrense = alder(source.nedreAldersgrense)
        )

    private fun alder(source: Alder) =
        PersonAlderV4(source.aar, source.maaneder)
}
