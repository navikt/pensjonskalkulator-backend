package no.nav.pensjon.kalkulator.tjenestepensjon.api.map

import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.TjenestepensjonsforholdDto

object TjenestepensjonMapper {
    fun toDto(harForhold: Boolean) = TjenestepensjonsforholdDto(harForhold)
}
