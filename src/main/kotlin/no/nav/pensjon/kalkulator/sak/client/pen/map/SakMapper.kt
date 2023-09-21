package no.nav.pensjon.kalkulator.sak.client.pen.map

import no.nav.pensjon.kalkulator.sak.Sak
import no.nav.pensjon.kalkulator.sak.client.pen.PenSakType
import no.nav.pensjon.kalkulator.sak.client.pen.PenSakStatus
import no.nav.pensjon.kalkulator.sak.client.pen.SakDto

object SakMapper {

    fun fromDto(dto: List<SakDto>): List<Sak> =
        dto.map {
            Sak(
                PenSakType.fromExternalValue(it.sakType).internalValue,
                PenSakStatus.fromExternalValue(it.sakStatus).internalValue
            )
        }
}
