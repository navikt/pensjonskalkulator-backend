package no.nav.pensjon.kalkulator.aldersgrense.api.map

import no.nav.pensjon.kalkulator.aldersgrense.api.dto.AldersgrenseResponse
import no.nav.pensjon.kalkulator.aldersgrense.api.dto.PersonAlder
import no.nav.pensjon.kalkulator.person.api.dto.PersonPensjoneringAldreV4

object AldersgrenseMapperV1 {
    fun dtoV1(source: PersonPensjoneringAldreV4): AldersgrenseResponse =
        AldersgrenseResponse(
            normertPensjoneringsalder = PersonAlder(source.normertPensjoneringsalder.aar, source.normertPensjoneringsalder.maaneder),
            nedreAldersgrense = PersonAlder(source.nedreAldersgrense.aar, source.nedreAldersgrense.maaneder)
        )
}
