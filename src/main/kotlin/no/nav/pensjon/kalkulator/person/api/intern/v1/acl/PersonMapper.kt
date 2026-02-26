package no.nav.pensjon.kalkulator.person.api.intern.v1.acl

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.person.Person

object PersonMapper {

    fun toDto(source: Person) =
        PersonInternV1Person(
            navn = source.navn,
            foedselsdato = source.foedselsdato,
            sivilstatus = PersonInternV1Sivilstatus.fromInternalValue(source.sivilstatus),
            pensjoneringAldre = source.pensjoneringAldre.let(::pensjonsaldre)
        )

    private fun pensjonsaldre(source: Aldersgrenser) =
        PersonInternV1Pensjonsaldre(
            normertPensjoneringsalder = alder(source.normalder),
            nedreAldersgrense = alder(source.nedreAlder),
            oevreAldersgrense = alder(source.oevreAlder),
        )

    private fun alder(source: Alder) =
        PersonInternV1Alder(source.aar, source.maaneder)
}
