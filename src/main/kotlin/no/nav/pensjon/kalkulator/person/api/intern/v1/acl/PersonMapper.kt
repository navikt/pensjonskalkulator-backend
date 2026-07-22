package no.nav.pensjon.kalkulator.person.api.intern.v1.acl

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.person.Person

/**
 * Maps from the domain representation of 'person' to a transferable representation (data transfer object).
 */
object PersonMapper {

    fun transferable(source: Person) =
        PersonInternV1Person(
            navn = source.navn,
            foedselsdato = source.foedselsdato,
            sivilstand = PersonInternV1Sivilstand.fromInternalValue(source.sivilstand),
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
        PersonInternV1Alder(
            aar = source.aar,
            maaneder = source.maaneder
        )
}