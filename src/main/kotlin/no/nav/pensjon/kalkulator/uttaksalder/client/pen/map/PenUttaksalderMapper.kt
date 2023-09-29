package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenSimuleringstype
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderEgressSpecDto
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderDto

object PenUttaksalderMapper {

    private const val MAANEDER_PER_AAR = 12

    fun fromDto(dto: UttaksalderDto) =
        Alder(
            aar = if (dto.maaned == 0) dto.aar - 1 else dto.aar,
            maaneder = oneBasedMaaned(dto.maaned) - 1
        )

    fun toDto(spec: UttaksalderSpec) =
        UttaksalderEgressSpecDto(
            pid = spec.pid.value,
            sivilstand = PenSivilstand.fromInternalValue(spec.sivilstand).externalValue,
            harEps = spec.harEps,
            sisteInntekt = spec.sisteInntekt,
            simuleringType = PenSimuleringstype.fromInternalValue(spec.simuleringType).externalValue,
        )

    private fun oneBasedMaaned(zeroBasedMaaned: Int) =
        if (zeroBasedMaaned == 0) MAANEDER_PER_AAR else zeroBasedMaaned
}
