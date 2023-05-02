package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenSivilstand
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimuleringRequestDto
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimuleringResponseDto
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

object SimuleringMapper {

    fun fromDto(dto: SimuleringResponseDto): Simuleringsresultat =
        dto.pensjon.let {
            Simuleringsresultat(2033, it.belop.toBigDecimal(), it.alder)
        }

    fun toDto(spec: SimuleringSpec) =
        SimuleringRequestDto(
            spec.pid.value,
            toDto(spec.sivilstand),
            spec.epsHarInntektOver2G,
            1,
            spec.forventetInntekt,
            midnight(spec.foersteUttaksdato)
        )

    private fun midnight(date: LocalDate) = Date.from(date.atTime(0, 0).toInstant(ZoneOffset.ofHours(1)))

    private fun toDto(sivilstand: Sivilstand) =
        when (sivilstand) {
            Sivilstand.UGIFT -> PenSivilstand.UGIF
            Sivilstand.OTHER -> PenSivilstand.NULL
        }
}
