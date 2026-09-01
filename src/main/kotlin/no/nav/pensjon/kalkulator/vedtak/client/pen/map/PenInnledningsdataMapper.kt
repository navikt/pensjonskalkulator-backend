package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import no.nav.pensjon.kalkulator.vedtak.Innledningsdata
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenInnledningsdataDto

object PenInnledningsdataMapper {
    fun fromDto(source: PenInnledningsdataDto) =
        Innledningsdata(harGjenlevenderett = source.isGjenlevenderett)
}
