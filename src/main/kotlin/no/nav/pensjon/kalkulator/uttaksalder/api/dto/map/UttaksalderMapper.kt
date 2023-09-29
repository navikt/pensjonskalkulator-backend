package no.nav.pensjon.kalkulator.uttaksalder.api.dto.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.AlderDto

object UttaksalderMapper {

    fun toDto(uttaksalder: Alder?): AlderDto? = uttaksalder?.let { toAlderDto(uttaksalder) }

    private fun toAlderDto(uttaksalder: Alder) = AlderDto(uttaksalder.aar, uttaksalder.maaneder)
}
