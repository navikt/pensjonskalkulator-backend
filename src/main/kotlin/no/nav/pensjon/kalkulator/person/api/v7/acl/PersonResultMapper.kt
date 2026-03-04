package no.nav.pensjon.kalkulator.person.api.v7.acl

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService.Companion.defaultAldersgrenser
import no.nav.pensjon.kalkulator.person.Person
import java.time.LocalDate

object PersonResultMapper {

    fun toDto(source: Person?): PersonV7Result =
        source?.let(::person) ?: defaultPerson()

    private fun person(source: Person) =
        PersonV7Result(
            navn = source.navn,
            fornavn = source.fornavn,
            foedselsdato = source.foedselsdato,
            sivilstatus = PersonV7Sivilstatus.fromInternalValue(source.sivilstatus),
            pensjoneringAldre = source.pensjoneringAldre.let(::pensjonsaldre)
        )

    private fun defaultPerson() =
        PersonV7Result(
            navn = "",
            fornavn = "",
            foedselsdato = LocalDate.MIN,
            sivilstatus = PersonV7Sivilstatus.UOPPGITT,
            pensjoneringAldre = defaultAldersgrenser.let(::pensjonsaldre)
        )

    private fun pensjonsaldre(source: Aldersgrenser) =
        PersonV7Pensjonsaldre(
            normertPensjoneringsalder = alder(source.normalder),
            nedreAldersgrense = alder(source.nedreAlder),
            oevreAldersgrense = alder(source.oevreAlder),
        )

    private fun alder(source: Alder) =
        PersonV7Alder(
            aar = source.aar,
            maaneder = source.maaneder
        )
}
