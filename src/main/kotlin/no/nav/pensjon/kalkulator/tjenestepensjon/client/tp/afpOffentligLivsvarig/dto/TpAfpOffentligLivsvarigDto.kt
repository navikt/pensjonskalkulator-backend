package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.afpOffentligLivsvarig.dto

import java.time.LocalDate

data class TpAfpOffentligLivsvarigDto(
    val statusAfp: AfpStatus,
    val virkningsDato: LocalDate?,
    val sistBenyttetG: Int?,
    val belopsListe: List<BelopItem>
)

enum class AfpStatus {
    UKJENT,
    IKKE_SOKT,
    SOKT,
    INNVILGET,
    AVSLAG
}

data class BelopItem(
    val fomDato: LocalDate,
    val belop: Int
)
