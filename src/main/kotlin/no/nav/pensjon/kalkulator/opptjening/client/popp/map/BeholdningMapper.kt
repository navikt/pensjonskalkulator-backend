package no.nav.pensjon.kalkulator.opptjening.client.popp.map

import no.nav.pensjon.kalkulator.opptjening.AarligBeholdning
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PoppBeholdning
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PoppBeholdningResult
import no.nav.pensjon.kalkulator.tech.time.toNorwegianLocalDate

object BeholdningMapper {

    fun fromDto(dto: PoppBeholdningResult): List<AarligBeholdning> =
        dto.beholdninger.map(::beholdning)

    private fun beholdning(dto: PoppBeholdning) =
        AarligBeholdning(
            aar = dto.fomDato.toNorwegianLocalDate().year,
            beholdning = dto.belop.toInt()
        )
}