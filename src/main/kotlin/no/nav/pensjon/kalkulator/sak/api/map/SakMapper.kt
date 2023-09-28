package no.nav.pensjon.kalkulator.sak.api.map

import no.nav.pensjon.kalkulator.sak.api.dto.SakDto

object SakMapper {
    fun toDto(harSak: Boolean) = SakDto(harSak)
}
