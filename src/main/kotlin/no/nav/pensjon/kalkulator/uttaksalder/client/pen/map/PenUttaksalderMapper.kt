package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.common.client.pen.PenUttaksgrad
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenSimuleringType
import no.nav.pensjon.kalkulator.tech.time.DateUtil.MAANEDER_PER_AAR
import no.nav.pensjon.kalkulator.tech.time.DateUtil.toDate
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderDto
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderEgressSpecDto

object PenUttaksalderMapper {

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
            simuleringType = PenSimuleringType.fromInternalValue(spec.simuleringType).externalValue,
            uttaksgrad = spec.gradertUttak?.let { PenUttaksgrad.fromInternalValue(it.grad).externalValue },
            inntektUnderGradertUttak = spec.gradertUttak?.inntektUnderGradertUttak,
            heltUttakDato = spec.gradertUttak?.let { toDate(it.heltUttakDato) }
        )

    private fun oneBasedMaaned(zeroBasedMaaned: Int) =
        if (zeroBasedMaaned == 0) MAANEDER_PER_AAR else zeroBasedMaaned
}
