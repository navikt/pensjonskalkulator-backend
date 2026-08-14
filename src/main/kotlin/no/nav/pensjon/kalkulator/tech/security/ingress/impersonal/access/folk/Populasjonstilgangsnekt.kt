package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk

import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.AvvisningAarsak

data class Populasjonstilgangsnekt(
    val aarsak: AvvisningAarsak,
    val begrunnelse: String
)