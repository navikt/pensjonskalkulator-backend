package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.PenUttaksalderResult

object PenUttaksalderResultMapper {

    fun fromDto(dto: PenUttaksalderResult) =
        Alder(
            aar = dto.alder.aar,
            maaneder = dto.alder.maaneder
        )
}
