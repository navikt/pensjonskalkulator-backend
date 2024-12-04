package no.nav.pensjon.kalkulator.uttaksalder.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderResultV2

object UttaksalderResultMapperV2 {

    fun resultV2(source: Alder?): UttaksalderResultV2? = source?.let(::alderResult)

    private fun alderResult(source: Alder) =
        UttaksalderResultV2(aar = source.aar, maaneder = source.maaneder)
}
