package no.nav.pensjon.kalkulator.tech.security.egress.token

class NoTokenGetter : EgressTokenGetter {
    override fun getEgressToken(ingressToken: String?, audience: String) =
        RawJwt("")
}