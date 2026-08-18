package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access

data class TilgangResult(
    val innvilget: Boolean,
    val avvisningAarsak: AvvisningAarsak? = null,
    val begrunnelse: String? = null,
    val traceId: String? = null
)