package no.nav.pensjon.kalkulator.sak.client.pen.map

import no.nav.pensjon.kalkulator.sak.Sak
import no.nav.pensjon.kalkulator.sak.client.pen.PenSakType
import no.nav.pensjon.kalkulator.sak.client.pen.PenSakStatus
import no.nav.pensjon.kalkulator.sak.client.pen.PenSak

object SakMapper {

    fun fromDto(dto: PenSak) =
        Sak(
            sakId = dto.sakId,
            type = PenSakType.internalValue(externalValue = dto.sakType),
            status = PenSakStatus.internalValue(externalValue = dto.sakStatus)
        )
}