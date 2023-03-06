package no.nav.pensjon.kalkulator.grunnbeloep.client.regler.map

import no.nav.pensjon.kalkulator.grunnbeloep.Grunnbeloep
import no.nav.pensjon.kalkulator.grunnbeloep.client.GrunnbeloepSpec
import no.nav.pensjon.kalkulator.grunnbeloep.client.regler.dto.GrunnbeloepRequestDto
import no.nav.pensjon.kalkulator.grunnbeloep.client.regler.dto.GrunnbeloepResponseDto
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

object GrunnbeloepMapper {

    fun toDto(spec: GrunnbeloepSpec): GrunnbeloepRequestDto {
        return GrunnbeloepRequestDto(toDto(spec.fom), toDto(spec.tom))
    }

    fun fromDto(dto: GrunnbeloepResponseDto): Grunnbeloep {
        val resultater = dto.satsResultater ?: emptyList()
        return if (resultater.isEmpty()) Grunnbeloep(0) else Grunnbeloep(resultater[0].verdi.intValueExact())
    }

    private fun toDto(date: LocalDate): Date = Date.from(date.atTime(12, 0).toInstant(ZoneOffset.ofHours(2)))
}
