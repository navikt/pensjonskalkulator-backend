package no.nav.pensjon.kalkulator.person.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService.Companion.defaultAldersgrenser
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.api.dto.PersonAlderV6
import no.nav.pensjon.kalkulator.person.api.dto.PersonPensjonsaldreV6
import no.nav.pensjon.kalkulator.person.api.dto.PersonResultV6
import no.nav.pensjon.kalkulator.person.api.dto.PersonSivilstandV6
import java.time.LocalDate

object PersonMapperV6 {

    fun dtoV6(source: Person?): PersonResultV6 =
        source?.let(::person) ?: defaultPerson()

    private fun person(source: Person) =
        PersonResultV6(
            navn = source.navn,
            fornavn = source.fornavn,
            foedselsdato = source.foedselsdato,
            sivilstand = PersonSivilstandV6.fromInternalValue(source.sivilstand),
            pensjoneringAldre = source.pensjoneringAldre.let(::pensjonsaldre)
        )

    private fun defaultPerson() =
        PersonResultV6(
            navn = "",
            fornavn = "",
            foedselsdato = LocalDate.MIN,
            sivilstand = PersonSivilstandV6.UOPPGITT,
            pensjoneringAldre = defaultAldersgrenser.let(::pensjonsaldre)
        )

    private fun pensjonsaldre(source: Aldersgrenser) =
        PersonPensjonsaldreV6(
            normertPensjoneringsalder = alder(source.normalder),
            nedreAldersgrense = alder(source.nedreAlder),
            oevreAldersgrense = alder(source.oevreAlder),
        )

    private fun alder(source: Alder) =
        PersonAlderV6(source.aar, source.maaneder)
}
