package no.nav.pensjon.kalkulator.person.api.intern.v1.acl

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.person.Person

object PersonMapper {

    fun toDto(source: Person) =
        PersonDto(
            navn = source.navn,
            foedselsdato = source.foedselsdato,
            sivilstatus = SivilstatusDto.fromInternalValue(source.sivilstatus),
            pensjoneringAldre = source.pensjoneringAldre.let(::pensjonsaldre)
        )

    private fun pensjonsaldre(source: Aldersgrenser) =
        PensjonsaldreDto(
            normertPensjoneringsalder = alder(source.normalder),
            nedreAldersgrense = alder(source.nedreAlder),
            oevreAldersgrense = alder(source.oevreAlder),
        )

    private fun alder(source: Alder) =
        AlderDto(source.aar, source.maaneder)
}
