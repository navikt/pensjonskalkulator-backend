package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.map

import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.TpTjenestepensjonDto

object TpTjenestepensjonMapper {

    fun fromDto(dto: TpTjenestepensjonDto) = dto.value
}
