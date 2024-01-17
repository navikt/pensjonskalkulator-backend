package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.common.client.pen.PenUttaksgrad
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.Inntekt
import no.nav.pensjon.kalkulator.general.UttaksalderGradertUttak
import no.nav.pensjon.kalkulator.simulering.PensjonUtil.uttakDato
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenSimuleringType
import no.nav.pensjon.kalkulator.tech.time.DateUtil.MAANEDER_PER_AAR
import no.nav.pensjon.kalkulator.tech.time.DateUtil.toDate
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.PersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.*
import java.util.*

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
            uttaksgrad = impersonalSpec.gradertUttak?.let { PenUttaksgrad.fromInternalValue(it.grad).externalValue }, // deprecated - replaced by gradertUttak.grad
            heltUttakDato = heltUttakDato(impersonalSpec), // deprecated - replaced by heltUttak.uttakFomAlder
            gradertUttak = impersonalSpec.gradertUttak?.let(::gradertUttakDto),
            heltUttak = impersonalSpec.heltUttak.inntekt?.let { heltUttakDto(impersonalSpec.heltUttak) }
        )

    private fun gradertUttakDto(uttak: UttaksalderGradertUttak) =
        UttaksalderGradertUttakSpecDto(
            grad = PenUttaksgrad.fromInternalValue(uttak.grad).externalValue,
            aarligInntekt = uttak.aarligInntekt
        )

    // deprecated - replaced by heltUttak.uttakFomAlder
    private fun heltUttakDato(spec: ImpersonalUttaksalderSpec): Date? =
        spec.gradertUttak?.let { toDate(uttakDato(it.foedselDato, spec.heltUttak.uttakFomAlder)) }

    private fun heltUttakDto(uttak: HeltUttak) =
        UttaksalderHeltUttakSpecDto(
            uttakFomAlder = alderDto(uttak.uttakFomAlder),
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
