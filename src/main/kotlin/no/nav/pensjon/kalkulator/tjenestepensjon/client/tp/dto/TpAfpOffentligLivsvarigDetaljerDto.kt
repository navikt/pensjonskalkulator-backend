package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto

import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.TpAfpStatusType
import java.time.LocalDate

data class TpAfpOffentligLivsvarigDetaljerDto(
    val statusAfp: TpAfpStatusType,
    val virkningsDato: LocalDate?,
    val sistBenyttetG: Int?,
    val belopsListe: List<BelopItem>
)

data class BelopItem(
    val fomDato: LocalDate,
    val belop: Int
)
