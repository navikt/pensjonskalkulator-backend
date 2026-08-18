package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.client.tilgangsmaskin.acl

/**
 * Ref. tilgangsmaskin.intern.nav.no/swagger-ui/index.html#/TilgangController/kompletteRegler
 */
data class ProblemDetailResponseDto(
    val type: String = "",
    val title: String?, // using String instead of AvvisningsKodeDto to handle unexpected enum values
    val status: Int = 0,
    val instance: String = "",
    val brukerIdent: String = "",
    val navIdent: String = "",
    val traceId: String = "",
    val begrunnelse: String = "",
    val kanOverstyres: Boolean = false
)