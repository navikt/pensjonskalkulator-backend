package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.jwtbearer

interface JwtBearerAssertionBasis {
    val clientId: String
    val clientJwk: String
    val issuer: String
}
