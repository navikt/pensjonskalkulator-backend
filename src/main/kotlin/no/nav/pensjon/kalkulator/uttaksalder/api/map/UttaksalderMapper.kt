package no.nav.pensjon.kalkulator.uttaksalder.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.AlderDto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.AlderIngressDto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderGradertUttakIngressDto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderIngressSpecDto

object UttaksalderMapper {

    fun toDto(uttaksalder: Alder?): AlderDto? = uttaksalder?.let(::alderDto)

    fun fromIngressSpecDto(spec: UttaksalderIngressSpecDto) =
        ImpersonalUttaksalderSpec(
            sivilstand = spec.sivilstand,
            harEps = spec.harEps,
            sisteInntekt = spec.sisteInntekt,
            simuleringType = spec.simuleringstype,
            gradertUttak = spec.gradertUttak?.let(::gradertUttak)
        )

    private fun gradertUttak(dto: UttaksalderGradertUttakIngressDto) =
        GradertUttak(
            grad = Uttaksgrad.from(dto.uttaksgrad),
            inntektUnderGradertUttak = dto.inntektUnderGradertUttak ?: 0,
            heltUttakAlder = alder(dto.heltUttakAlder),
            foedselDato = dto.foedselsdato
        )

    private fun alder(dto: AlderIngressDto) = Alder(dto.aar, dto.maaneder)

    private fun alderDto(alder: Alder) = AlderDto(alder.aar, alder.maaneder)
}
