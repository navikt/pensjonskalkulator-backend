package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.common.client.pen.PenUttaksgrad
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymAlderSpec
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymGradertUttakSpec
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymHeltUttakSpec
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymSimuleringSpec

object PenAnonymSimuleringSpecMapper {

    fun toDto(spec: ImpersonalSimuleringSpec) =
        PenAnonymSimuleringSpec(
            simuleringType = PenSimuleringType.fromInternalValue(spec.simuleringType).externalValue,
            foedselAar = spec.foedselAar ?: throw IllegalArgumentException("Undefined foedselAar"),
            sivilstand = PenSivilstand.fromInternalValue(spec.sivilstand).externalValue,
            epsHarInntektOver2G = spec.eps.harInntektOver2G,
            epsHarPensjon = spec.eps.harPensjon,
            utenlandsAntallAar = spec.utenlandsopphold.antallAar ?: 0,
            inntektOver1GAntallAar = spec.inntektOver1GAntallAar ?: 0,
            forventetAarligInntektFoerUttak = spec.forventetAarligInntektFoerUttak ?: 0,
            gradertUttak = spec.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(spec.heltUttak)
        )

    private fun gradertUttak(uttak: GradertUttak) =
        PenAnonymGradertUttakSpec(
            grad = PenUttaksgrad.fromInternalValue(uttak.grad).externalValue,
            uttakFomAlder = alder(uttak.uttakFomAlder),
            aarligInntekt = uttak.aarligInntekt
        )

    private fun heltUttak(uttak: HeltUttak) =
        PenAnonymHeltUttakSpec(
            uttakFomAlder = uttak.uttakFomAlder?.let(::alder)
                ?: throw IllegalArgumentException("Undefined uttakFomAlder (heltUttak)"),
            aarligInntekt = uttak.inntekt?.aarligBeloep ?: 0,
            inntektTomAlder = alder(uttak.inntekt?.tomAlder ?: uttak.uttakFomAlder)
        )

    private fun alder(alder: Alder) =
        PenAnonymAlderSpec(alder.aar, alder.maaneder)
}
