package no.nav.pensjon.kalkulator.avtale.api.map

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleDto
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtalerDto

object PensjonsavtaleMapper {

    fun toDto(source: Pensjonsavtaler) = PensjonsavtalerDto(source.liste.map(::pensjonsavtale))

    private fun pensjonsavtale(source: Pensjonsavtale) = PensjonsavtaleDto(source.navn, source.fom, source.tom)
}
