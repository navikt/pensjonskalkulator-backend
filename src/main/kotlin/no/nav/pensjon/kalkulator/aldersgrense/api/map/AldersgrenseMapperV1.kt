package no.nav.pensjon.kalkulator.aldersgrense.api.map

import no.nav.pensjon.kalkulator.aldersgrense.api.dto.AldersgrenseResultV1
import no.nav.pensjon.kalkulator.aldersgrense.api.dto.PersonAlder
import no.nav.pensjon.kalkulator.person.api.dto.PersonPensjoneringAldreV4

object AldersgrenseMapperV1 {
    fun dtoV1(source: PersonPensjoneringAldreV4): AldersgrenseResultV1 =
        AldersgrenseResultV1(
            normertPensjoneringsalder = PersonAlder(source.normertPensjoneringsalder.aar, source.normertPensjoneringsalder.maaneder),
            nedreAldersgrense = PersonAlder(source.nedreAldersgrense.aar, source.nedreAldersgrense.maaneder)
        )
}
