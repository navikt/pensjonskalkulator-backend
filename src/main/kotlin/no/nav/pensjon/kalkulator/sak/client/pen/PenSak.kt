package no.nav.pensjon.kalkulator.sak.client.pen

/**
 * Corresponds to
 * no.nav.pensjon.pen.domain.api.sak.SakSammendragV2
 * in pensjon-pen
 */
data class PenSak(
    val sakId: Long,
    val sakType: String,
    val sakStatus: String
)