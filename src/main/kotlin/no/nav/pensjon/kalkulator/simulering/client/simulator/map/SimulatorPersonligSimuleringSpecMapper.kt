package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.InnvilgetLivsvarigOffentligAfpSpec
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.*

object SimulatorPersonligSimuleringSpecMapper {

    fun toDto(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ) =
        SimulatorSimuleringSpec(
            simuleringstype = SimulatorSimuleringType.fromInternalValue(impersonalSpec.simuleringType).externalValue,
            pid = personalSpec.pid.value,
            sivilstand = SimulatorSivilstand.fromInternalValue(personalSpec.sivilstand).externalValue,
            epsHarInntektOver2G = impersonalSpec.eps.levende?.harInntektOver2G == true,
            epsHarPensjon = impersonalSpec.eps.levende?.harPensjon == true,
            sisteInntekt = personalSpec.aarligInntektFoerUttak,
            uttaksar = 1,
            gradertUttak = impersonalSpec.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(impersonalSpec.heltUttak),
            utenlandsperiodeListe = impersonalSpec.utenlandsopphold.periodeListe.map(::utlandPeriode),
            afpInntektMaanedFoerUttak = impersonalSpec.afpInntektMaanedFoerUttak,
            afpOrdning = SimulatorAfpOrdningType.fromInternalValue(impersonalSpec.afpOrdning).externalValue,
            innvilgetLivsvarigOffentligAfp = impersonalSpec.innvilgetLivsvarigOffentligAfp?.let(::afp)
        )

    private fun gradertUttak(source: GradertUttak) =
        SimulatorGradertUttakSpec(
            grad = SimulatorUttaksgrad.fromInternalValue(source.grad).externalValue,
            uttakFomAlder = alder(source.uttakFomAlder),
            aarligInntekt = source.aarligInntekt
        )

    private fun heltUttak(source: HeltUttak) =
        SimulatorHeltUttakSpec(
            uttakFomAlder = alder(source.uttakFomAlder!!), // mandatory in the context of simulering
            aarligInntekt = source.inntekt?.aarligBeloep ?: 0,
            inntektTomAlder = source.inntekt?.let { alder(it.tomAlder) } ?: alder(source.uttakFomAlder)
        )

    private fun utlandPeriode(source: Opphold) =
        SimulatorUtlandPeriodeSpec(
            fom = source.fom,
            tom = source.tom,
            land = source.land.name,
            arbeidetUtenlands = source.arbeidet
        )

    private fun afp(source: InnvilgetLivsvarigOffentligAfpSpec) =
        SimulatorInnvilgetLivsvarigOffentligAfpSpec(
            aarligBruttoBeloep = source.aarligBruttoBeloep,
            uttakFom = source.uttakFom,
            sistRegulertGrunnbeloep = source.sistRegulertGrunnbeloep
        )

    private fun alder(source: Alder) =
        SimulatorAlderSpec(source.aar, source.maaneder)
}
