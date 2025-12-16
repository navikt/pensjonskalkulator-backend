package no.nav.pensjon.kalkulator.uttaksalder.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderResultV3

object UttaksalderResultMapperV3 {

    fun resultV3(source: Alder?): UttaksalderResultV3? =
        source?.let(::alder)

    private fun alder(source: Alder) =
        UttaksalderResultV3(aar = source.aar, maaneder = source.maaneder)
}
