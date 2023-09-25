package no.nav.pensjon.kalkulator.uttaksalder.api.dto.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderV0Dto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.AlderDto

object UttaksalderMapper {

    private const val MAANEDER_PER_AAR = 12

    fun toV0Dto(uttaksalder: Alder) = nesteMaaned(uttaksalder)

    fun toV1Dto(uttaksalder: Alder) =
        AlderDto(uttaksalder.aar, uttaksalder.maaneder)

    private fun nesteMaaned(alder: Alder) =
        UttaksalderV0Dto(
            aar = if (alder.maaneder == MAANEDER_PER_AAR) alder.aar + 1 else alder.aar,
            maaned = if (alder.maaneder == MAANEDER_PER_AAR) 0 else alder.maaneder + 1
        )
}
