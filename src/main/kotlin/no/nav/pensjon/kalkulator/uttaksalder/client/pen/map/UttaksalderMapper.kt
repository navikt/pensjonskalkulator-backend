package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.uttaksalder.Uttaksalder
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderRequestDto
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderResponseDto

object UttaksalderMapper {

    fun fromDto(dto: UttaksalderResponseDto) =
        Uttaksalder(aar = dto.aar, maaned = dto.maaned)

    fun toDto(spec: UttaksalderSpec) =
        UttaksalderRequestDto(
            pid = spec.pid.value,
            sivilstand = PenSivilstand.from(spec.sivilstand),
            harEps = spec.harEps,
            sisteInntekt = spec.sisteInntekt,
        )
}
