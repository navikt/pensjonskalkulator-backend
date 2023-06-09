package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.pen.PenSivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.SimulertAfpPrivat
import no.nav.pensjon.kalkulator.simulering.SimulertAlderspensjon
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimuleringRequestDto
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimuleringResponseDto
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

object SimuleringMapper {

    fun fromDto(dto: SimuleringResponseDto): Simuleringsresultat =
        Simuleringsresultat(
            alderspensjon = dto.alderspensjon.map { SimulertAlderspensjon(alder = it.alder, belop = it.belop) },
            afpPrivat = dto.afpPrivat.map { SimulertAfpPrivat(alder = it.alder, belop = it.belop) }
        )

    fun toDto(spec: SimuleringSpec) =
        SimuleringRequestDto(
            pid = spec.pid.value,
            sivilstand = PenSivilstand.from(spec.sivilstand),
            harEps = spec.epsHarInntektOver2G,
            uttaksar = 1,
            sisteInntekt = spec.forventetInntekt,
            forsteUttaksdato = midnight(spec.foersteUttaksdato)
        )

    private fun midnight(date: LocalDate) = Date.from(date.atTime(0, 0).toInstant(ZoneOffset.ofHours(1)))
}
