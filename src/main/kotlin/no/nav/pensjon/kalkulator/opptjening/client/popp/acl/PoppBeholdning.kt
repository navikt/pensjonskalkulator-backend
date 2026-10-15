package no.nav.pensjon.kalkulator.opptjening.client.popp.acl

import no.nav.pensjon.kalkulator.opptjening.DatertBeholdning
import no.nav.pensjon.kalkulator.tech.time.toNorwegianLocalDate
import java.util.Date

data class PoppBeholdning(
    val belop: Double,
    val oppdateringArsak: String?,
    val fomDato: Date
) {
    fun toInternalValue() =
        DatertBeholdning(
            dato = fomDato.toNorwegianLocalDate(),
            beholdning = belop.toInt(),
            oppdateringsaarsak = PoppBeholdningsoppdateringsaarsak.internalValue(oppdateringArsak)
        )
}