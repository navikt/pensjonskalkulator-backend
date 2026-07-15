package no.nav.pensjon.kalkulator.opptjening.client.popp.dto

/**
 * Data transfer object for the specificatinn (parameters) of a request to the pensjon-popp beholdning API.
 * Field names are dictated by pensjon-popp.
 */
data class PoppBeholdningSpec(
    val fnr: String,
    val beholdningType: String = "PEN_B",
    val serviceDirectiveTPOPP006: String = "INKL_GRUNNLAG"
)