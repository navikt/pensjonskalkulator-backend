package no.nav.pensjon.kalkulator.opptjening.client.popp.acl

import no.nav.pensjon.kalkulator.opptjening.DatertBeholdning

/**
 * Data transfer object for the result of a request to the pensjon-popp beholdning API.
 * Field names are dictated by pensjon-popp.
 */
data class PoppBeholdningResult(
    val beholdninger: List<PoppBeholdning>
) {
    fun toInternalValue(): List<DatertBeholdning> =
        beholdninger.map { it.toInternalValue() }
}