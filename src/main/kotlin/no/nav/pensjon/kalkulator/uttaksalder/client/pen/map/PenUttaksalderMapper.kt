package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.common.client.pen.PenUttaksgrad
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.Inntekt
import no.nav.pensjon.kalkulator.general.UttaksalderGradertUttak
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenSimuleringType
import no.nav.pensjon.kalkulator.tech.time.DateUtil.MAANEDER_PER_AAR
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.PersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.*

object PenUttaksalderMapper {

    private val DEFAULT_HELT_UTTAK_INNTEKT_TOM_ALDER = UttaksalderAlderDto(aar = 75, maaneder = 0)

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
            gradertUttak = impersonalSpec.gradertUttak?.let(::gradertUttakSpecDto),
            heltUttak = impersonalSpec.heltUttak?.let(::heltUttakSpecDto) ?: defaultHeltUttakSpecDto()
        )

    private fun defaultHeltUttakSpecDto() =
        UttaksalderHeltUttakSpecDto(
            uttakFomAlder = null,
            inntekt = UttaksalderInntektDto(
                aarligBelop = 0,
                tomAlder = DEFAULT_HELT_UTTAK_INNTEKT_TOM_ALDER
            )
        )

    private fun gradertUttakSpecDto(uttak: UttaksalderGradertUttak) =
        UttaksalderGradertUttakSpecDto(
            grad = PenUttaksgrad.fromInternalValue(uttak.grad).externalValue,
            aarligInntekt = uttak.aarligInntekt
        )

    private fun heltUttakSpecDto(uttak: HeltUttak) =
        UttaksalderHeltUttakSpecDto(
            uttakFomAlder = uttak.uttakFomAlder?.let(::alderDto),
            inntekt = uttak.inntekt?.let(::inntektDto)
        )

    private fun alderDto(alder: Alder) = UttaksalderAlderDto(alder.aar, alder.maaneder)

    private fun inntektDto(inntekt: Inntekt) =
        UttaksalderInntektDto(
            aarligBelop = inntekt.aarligBeloep,
            tomAlder = alderDto(inntekt.tomAlder)
        )

    private fun oneBasedMaaned(zeroBasedMaaned: Int) =
        if (zeroBasedMaaned == 0) MAANEDER_PER_AAR else zeroBasedMaaned
}
