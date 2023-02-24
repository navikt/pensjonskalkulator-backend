package no.nav.pensjon.kalkulator.tech.security.egress.token

interface EgressTokenGetter {
    fun getEgressToken(ingressToken: String, audience: String, user: String): RawJwt
}
