package no.nav.pensjon.kalkulator.normalder.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.api.dto.AldersgrenseResultV1
import no.nav.pensjon.kalkulator.normalder.api.dto.PersonAlder

object AldersgrenseMapperV1 {

    fun dtoV1(source: Aldersgrenser) =
        AldersgrenseResultV1(
            normertPensjoneringsalder = alder(source.normalder),
            nedreAldersgrense = alder(source.nedreAlder)
        )

    private fun alder(source: Alder) =
        PersonAlder(source.aar, source.maaneder)
}
