package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.*

object SimulatorSimuleringSpecMapper {

    fun toDto(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ) =
        SimulatorSimuleringSpec(
            simuleringstype = SimulatorSimuleringType.fromInternalValue(impersonalSpec.simuleringType).externalValue,
            pid = personalSpec.pid.value,
            sivilstand = SimulatorSivilstand.fromInternalValue(personalSpec.sivilstand).externalValue,
            epsHarInntektOver2G = impersonalSpec.eps.harInntektOver2G,
            epsHarPensjon = false, // NB: Ikke-støttet verdi
            sisteInntekt = personalSpec.aarligInntektFoerUttak,
            uttaksar = 1,
            gradertUttak = impersonalSpec.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(impersonalSpec.heltUttak),
            utenlandsperiodeListe = impersonalSpec.utenlandsopphold.periodeListe.map(::utlandPeriode)
        )

    private fun gradertUttak(spec: GradertUttak) =
        SimulatorGradertUttakSpec(
            grad = SimulatorUttaksgrad.fromInternalValue(spec.grad).externalValue,
            uttakFomAlder = alder(spec.uttakFomAlder),
            aarligInntekt = spec.aarligInntekt
        )

    private fun heltUttak(spec: HeltUttak) =
        SimulatorHeltUttakSpec(
            uttakFomAlder = alder(spec.uttakFomAlder!!), // mandatory in context of simulering
            aarligInntekt = spec.inntekt?.aarligBeloep ?: 0,
            inntektTomAlder = spec.inntekt?.let { alder(it.tomAlder) } ?: alder(spec.uttakFomAlder)
        )

    private fun utlandPeriode(spec: Opphold) =
        SimulatorUtlandPeriodeSpec(
            fom = spec.fom,
            tom = spec.tom,
            land = spec.land.name,
            arbeidetUtenlands = spec.arbeidet
        )

    private fun alder(spec: Alder) =
        SimulatorAlderSpec(spec.aar, spec.maaneder)
}
