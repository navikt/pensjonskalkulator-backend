package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.SimulatorAnonymAlderSpec
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.SimulatorAnonymSimuleringSpec
import java.time.LocalDate

object SimulatorAnonymSimuleringSpecMapper {

    fun toDto(spec: ImpersonalSimuleringSpec): SimulatorAnonymSimuleringSpec {
        val foedselsdato: LocalDate =
            spec.foedselAar?.let { LocalDate.of(it, 1, 1) }
                ?: throw IllegalArgumentException("Undefined foedselAar")

        val gradertUttakFom: LocalDate? =
            spec.gradertUttak?.uttakFomAlder?.let {
                SimulatorAnonymAlderDato(foedselsdato, alder(it)).dato
            }

        val heltUttakFom: LocalDate =
            SimulatorAnonymAlderDato(foedselsdato, alder = alder(spec.heltUttak.uttakFomAlder!!)).dato

        val inntektTom: LocalDate =
            SimulatorAnonymAlderDato(foedselsdato, alder = alder(spec.heltUttak.inntekt!!.tomAlder)).dato

        val uttaksgrad = spec.gradertUttak?.grad ?: Uttaksgrad.HUNDRE_PROSENT

        return SimulatorAnonymSimuleringSpec(
            simuleringType = SimulatorSimuleringType.fromInternalValue(spec.simuleringType).externalValue,
            fodselsar = spec.foedselAar,
            sivilstatus = SimulatorSivilstand.fromInternalValue(spec.sivilstand).externalValue,
            eps2G = spec.eps.levende?.harInntektOver2G == true,
            epsPensjon = spec.eps.levende?.harPensjon == true,
            utenlandsopphold = spec.utenlandsopphold.antallAar ?: 0,
            antArInntektOverG = spec.inntektOver1GAntallAar ?: 0,
            forventetInntekt = spec.forventetAarligInntektFoerUttak ?: 0,
            forsteUttakDato = gradertUttakFom ?: heltUttakFom,
            utg = uttaksgrad.let { SimulatorUttaksgrad.fromInternalValue(it).externalValue },
            inntektUnderGradertUttak = spec.gradertUttak?.aarligInntekt,
            heltUttakDato = heltUttakFom,
            inntektEtterHeltUttak = spec.heltUttak.inntekt.aarligBeloep,
            antallArInntektEtterHeltUttak = inntektTom.year - heltUttakFom.year
        )
    }

    /* May be used in future:
    fun toDto(spec: ImpersonalSimuleringSpec) =
        SimulatorAnonymSimuleringSpec(
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
        SimulatorAnonymGradertUttakSpec(
            grad = PenUttaksgrad.fromInternalValue(uttak.grad).externalValue,
            uttakFomAlder = alder(uttak.uttakFomAlder),
            aarligInntekt = uttak.aarligInntekt
        )

    private fun heltUttak(uttak: HeltUttak) =
        SimulatorAnonymHeltUttakSpec(
            uttakFomAlder = uttak.uttakFomAlder?.let(::alder)
                ?: throw IllegalArgumentException("Undefined uttakFomAlder (heltUttak)"),
            aarligInntekt = uttak.inntekt?.aarligBeloep ?: 0,
            inntektTomAlder = alder(uttak.inntekt?.tomAlder ?: uttak.uttakFomAlder)
        )
    */
    private fun alder(source: Alder) =
        SimulatorAnonymAlderSpec(source.aar, source.maaneder)
}
