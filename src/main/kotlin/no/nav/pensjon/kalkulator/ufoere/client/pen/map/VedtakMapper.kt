package no.nav.pensjon.kalkulator.ufoere.client.pen.map

import no.nav.pensjon.kalkulator.ufoere.Vedtak
import no.nav.pensjon.kalkulator.ufoere.client.pen.PenSakstype
import no.nav.pensjon.kalkulator.ufoere.client.pen.VedtakDto

object VedtakMapper {

    fun fromDto(dto: List<VedtakDto>): List<Vedtak> =
        dto.map { Vedtak(PenSakstype.fromExternalValue(it.sakstype).internalValue) }
}
