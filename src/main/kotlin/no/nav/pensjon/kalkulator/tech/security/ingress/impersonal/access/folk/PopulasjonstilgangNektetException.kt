package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk

class PopulasjonstilgangNektetException(
    message: String? = null,
    val aarsak: Populasjonstilgangsnekt
) : RuntimeException(message)