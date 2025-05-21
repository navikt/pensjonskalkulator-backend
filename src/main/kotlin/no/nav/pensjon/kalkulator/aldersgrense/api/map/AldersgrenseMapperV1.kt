package no.nav.pensjon.kalkulator.aldersgrense.api.map

import no.nav.pensjon.kalkulator.aldersgrense.api.dto.AldersgrenseResultV1
import no.nav.pensjon.kalkulator.aldersgrense.api.dto.PersonAlder
import no.nav.pensjon.kalkulator.uttaksalder.normalder.PensjoneringAldre

object AldersgrenseMapperV1 {
    fun dtoV1(source: PensjoneringAldre): AldersgrenseResultV1 =
        AldersgrenseResultV1(
            normertPensjoneringsalder = PersonAlder(source.nedreAldersgrense.aar, source.normalder.maaneder),
            nedreAldersgrense = PersonAlder(source.nedreAldersgrense.aar, source.nedreAldersgrense.maaneder)
        )
}
