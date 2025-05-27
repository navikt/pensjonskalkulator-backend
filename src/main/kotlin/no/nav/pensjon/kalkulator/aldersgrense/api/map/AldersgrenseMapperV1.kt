package no.nav.pensjon.kalkulator.aldersgrense.api.map

import no.nav.pensjon.kalkulator.aldersgrense.api.dto.AldersgrenseResultV1
import no.nav.pensjon.kalkulator.aldersgrense.api.dto.PersonAlder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser

object AldersgrenseMapperV1 {
    fun dtoV1(source: Aldersgrenser): AldersgrenseResultV1 =
        AldersgrenseResultV1(
            normertPensjoneringsalder = PersonAlder(source.normalder.aar, source.normalder.maaneder),
            nedreAldersgrense = PersonAlder(source.nedreAlder.aar, source.nedreAlder.maaneder)
        )
}
