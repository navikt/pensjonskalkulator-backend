package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.common.client.pen.PenUttaksgrad
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenSimuleringType
import no.nav.pensjon.kalkulator.tech.time.DateUtil.MAANEDER_PER_AAR
import no.nav.pensjon.kalkulator.tech.time.DateUtil.toDate
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.PersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderDto
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderEgressSpecDto

object PenUttaksalderMapper {

    fun fromDto(dto: UttaksalderDto) =
        Alder(
            aar = if (dto.maaned == 0) dto.aar - 1 else dto.aar,
            maaneder = oneBasedMaaned(dto.maaned) - 1
        )

    fun toDto(
        impersonalSpec: ImpersonalUttaksalderSpec,
        personalSpec: PersonalUttaksalderSpec
    ) =
        UttaksalderEgressSpecDto(
            simuleringType = PenSimuleringType.fromInternalValue(impersonalSpec.simuleringType).externalValue,
            pid = personalSpec.pid.value,
            sivilstand = PenSivilstand.fromInternalValue(personalSpec.sivilstand).externalValue,
            harEps = personalSpec.harEps,
            sisteInntekt = personalSpec.aarligInntektFoerUttak,
            uttaksgrad = impersonalSpec.gradertUttak?.let { PenUttaksgrad.fromInternalValue(it.grad).externalValue },
            inntektUnderGradertUttak = impersonalSpec.gradertUttak?.aarligInntekt,
            heltUttakDato = impersonalSpec.gradertUttak?.let { toDate(it.uttakFomDato) }
        )

    private fun oneBasedMaaned(zeroBasedMaaned: Int) =
        if (zeroBasedMaaned == 0) MAANEDER_PER_AAR else zeroBasedMaaned
}
