package no.nav.pensjon.kalkulator.opptjening.client.popp.map

import no.nav.pensjon.kalkulator.opptjening.DatertBeholdning
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PoppBeholdning
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PoppBeholdningsoppdateringsaarsak
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PoppBeholdningResult
import no.nav.pensjon.kalkulator.tech.time.toNorwegianLocalDate

object BeholdningMapper {

    fun fromDto(dto: PoppBeholdningResult): List<DatertBeholdning> =
        dto.beholdninger.map(::beholdning)

    private fun beholdning(dto: PoppBeholdning) =
        DatertBeholdning(
            dato = dto.fomDato.toNorwegianLocalDate(),
            beholdning = dto.belop.toInt(),
            oppdateringsaarsak = PoppBeholdningsoppdateringsaarsak.internalValue(dto.oppdateringArsak)
        )
}