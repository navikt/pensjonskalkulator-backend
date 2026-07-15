package no.nav.pensjon.kalkulator.ufoere.client.pen.map

import no.nav.pensjon.kalkulator.sak.client.pen.PenSakType
import no.nav.pensjon.kalkulator.ufoere.Vedtak
import no.nav.pensjon.kalkulator.ufoere.client.pen.VedtakDto

object VedtakMapper {

    fun fromDto(dto: List<VedtakDto>): List<Vedtak> =
        dto.map { Vedtak(sakstype = PenSakType.internalValue(externalValue = it.sakstype)) }
}