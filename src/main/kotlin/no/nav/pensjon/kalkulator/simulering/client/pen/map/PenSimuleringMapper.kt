package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.common.client.pen.PenUttaksgrad
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimuleringEgressSpecDto
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimuleringResponseDto
import no.nav.pensjon.kalkulator.tech.time.DateUtil.toDate

object PenSimuleringMapper {

    fun fromDto(dto: SimuleringResponseDto): Simuleringsresultat =
        Simuleringsresultat(
            alderspensjon = dto.alderspensjon.map { SimulertAlderspensjon(alder = it.alder, beloep = it.beloep) },
            afpPrivat = dto.afpPrivat.map { SimulertAfpPrivat(alder = it.alder, beloep = it.beloep) }
        )

    fun toDto(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ) =
        SimuleringEgressSpecDto(
            pid = personalSpec.pid.value,
            sivilstand = PenSivilstand.fromInternalValue(personalSpec.sivilstand).externalValue,
            harEps = impersonalSpec.epsHarInntektOver2G,
            uttaksar = 1,
            sisteInntekt = personalSpec.forventetInntekt,
            forsteUttaksdato = toDate(impersonalSpec.foersteUttakDato),
            simuleringstype = PenSimuleringType.fromInternalValue(impersonalSpec.simuleringType).externalValue,
            uttaksgrad = impersonalSpec.gradertUttak?.let { PenUttaksgrad.fromInternalValue(it.grad).externalValue },
            heltUttakDato = impersonalSpec.gradertUttak?.let { toDate(it.heltUttakDato) },
            inntektUnderGradertUttak = impersonalSpec.gradertUttak?.inntektUnderGradertUttak
        )
}
