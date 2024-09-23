package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.common.client.pen.PenUttaksgrad
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.*

object PenSimuleringSpecMapper {

    fun toDto(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ) =
        SimuleringEgressSpecDto(
            simuleringstype = PenSimuleringType.fromInternalValue(impersonalSpec.simuleringType).externalValue,
            pid = personalSpec.pid.value,
            sivilstand = PenSivilstand.fromInternalValue(personalSpec.sivilstand).externalValue,
            epsHarInntektOver2G = impersonalSpec.eps.harInntektOver2G,
            epsHarPensjon = false, // NB: Ikke-støttet verdi
            sisteInntekt = personalSpec.aarligInntektFoerUttak,
            uttaksar = 1,
            gradertUttak = impersonalSpec.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(impersonalSpec.heltUttak),
            utenlandsperiodeListe = impersonalSpec.utenlandsopphold.periodeListe.map(::utlandPeriode)
        )

    private fun gradertUttak(spec: GradertUttak) =
        GradertUttakSpecDto(
            grad = PenUttaksgrad.fromInternalValue(spec.grad).externalValue,
            uttakFomAlder = alder(spec.uttakFomAlder),
            aarligInntekt = spec.aarligInntekt
        )

    private fun heltUttak(spec: HeltUttak) =
        HeltUttakSpecDto(
            uttakFomAlder = alder(spec.uttakFomAlder!!), // mandatory in context of simulering
            aarligInntekt = spec.inntekt?.aarligBeloep ?: 0,
            inntektTomAlder = spec.inntekt?.let { alder(it.tomAlder) } ?: alder(spec.uttakFomAlder)
        )

    private fun utlandPeriode(spec: Opphold) =
        PenUtenlandsperiodeSpec(
            fom = spec.fom,
            tom = spec.tom,
            land = spec.land.name,
            arbeidetUtenlands = spec.arbeidet
        )

    private fun alder(spec: Alder) =
        AlderSpecDto(spec.aar, spec.maaneder)
}
