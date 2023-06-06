package no.nav.pensjon.kalkulator.avtale.client.pen.map

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.client.pen.dto.PensjonsavtaleDto
import no.nav.pensjon.kalkulator.avtale.client.pen.dto.PensjonsavtalerDto

object PensjonsavtaleMapper {

    fun fromDto(dto: PensjonsavtalerDto) = Pensjonsavtaler(dto.avtaler.map(::pensjonsavtale))

    private fun pensjonsavtale(dto: PensjonsavtaleDto) = Pensjonsavtale(dto.navn, dto.fom, dto.tom)
}
