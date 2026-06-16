package no.nav.pensjon.kalkulator.opptjening.client.popp.dto

import java.util.*

/**
 * Data transfer object for the result of a request to the pensjon-popp beholdning API.
 * Field names are dictated by pensjon-popp.
 */
data class PoppBeholdningResult(
    val beholdninger: List<PoppBeholdning>
)

data class PoppBeholdning(
    val belop: Double,
    val fomDato: Date
)