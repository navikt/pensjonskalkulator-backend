package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.common.client.pen.PenUttaksgrad
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.Inntekt
import no.nav.pensjon.kalkulator.general.UttaksalderGradertUttak
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenSimuleringType
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.PersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.*

object PenUttaksalderSpecMapper {

    private val DEFAULT_HELT_UTTAK_INNTEKT_TOM_ALDER = PenUttaksalderAlderSpec(aar = 75, maaneder = 0)

    fun toDto(
        impersonalSpec: ImpersonalUttaksalderSpec,
        personalSpec: PersonalUttaksalderSpec
    ) =
        PenUttaksalderSpec(
            simuleringType = PenSimuleringType.fromInternalValue(impersonalSpec.simuleringType).externalValue,
            pid = personalSpec.pid.value,
            sivilstand = PenSivilstand.fromInternalValue(personalSpec.sivilstand).externalValue,
            harEps = personalSpec.harEps,
            sisteInntekt = personalSpec.aarligInntektFoerUttak,
            gradertUttak = impersonalSpec.gradertUttak?.let(::gradertUttakSpec),
            heltUttak = impersonalSpec.heltUttak?.let(::heltUttakSpec) ?: defaultHeltUttakSpec()
        )

    private fun defaultHeltUttakSpec() =
        PenUttaksalderHeltUttakSpec(
            uttakFomAlder = null,
            inntekt = PenUttaksalderInntektSpec(
                aarligBelop = 0,
                tomAlder = DEFAULT_HELT_UTTAK_INNTEKT_TOM_ALDER
            )
        )

    private fun gradertUttakSpec(uttak: UttaksalderGradertUttak) =
        PenUttaksalderGradertUttakSpec(
            grad = PenUttaksgrad.fromInternalValue(uttak.grad).externalValue,
            aarligInntekt = uttak.aarligInntekt
        )

    private fun heltUttakSpec(uttak: HeltUttak) =
        PenUttaksalderHeltUttakSpec(
            uttakFomAlder = uttak.uttakFomAlder?.let(::alderSpec),
            inntekt = uttak.inntekt?.let(::inntektSpec)
        )

    private fun alderSpec(alder: Alder) =
        PenUttaksalderAlderSpec(
            aar = alder.aar,
            maaneder = alder.maaneder
        )

    private fun inntektSpec(inntekt: Inntekt) =
        PenUttaksalderInntektSpec(
            aarligBelop = inntekt.aarligBeloep,
            tomAlder = alderSpec(inntekt.tomAlder)
        )
}
