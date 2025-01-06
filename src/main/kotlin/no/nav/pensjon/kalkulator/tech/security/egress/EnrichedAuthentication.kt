package no.nav.pensjon.kalkulator.tech.security.egress

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentertRolle
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.security.egress.token.RawJwt
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

/**
 * Authentication data is initially obtained by Spring Security.
 * This class augments that data by adding a mechanism for obtaining egress tokens
 * (used by backend for accessing other services).
 * It also keeps the person ID (if available).
 */
class EnrichedAuthentication(
    private val initialAuth: Authentication?,
    private val egressTokenSuppliersByService: EgressTokenSuppliersByService,
    private val target: RepresentasjonTarget
) : Authentication {

    fun getEgressAccessToken(service: EgressService, ingressToken: String?): RawJwt =
        egressTokenSuppliersByService.value[service]?.apply(ingressToken) ?: RawJwt("")

    fun needFulltNavn(): Boolean = target.rolle.needFulltNavn

    fun veilederInnlogget(): Boolean = target.rolle == RepresentertRolle.UNDER_VEILEDNING

    fun getClaim(key: String): String? = (credentials as? Jwt)?.claims[key] as? String

    fun targetPid(): Pid? = target.pid

    override fun getName(): String = initialAuth?.name ?: ""

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = initialAuth?.authorities ?: mutableSetOf()

    override fun getCredentials(): Any = initialAuth?.credentials ?: ""

    override fun getDetails(): Any = initialAuth?.details ?: ""

    override fun getPrincipal(): Any = initialAuth?.principal ?: ""

    override fun isAuthenticated(): Boolean = initialAuth?.isAuthenticated ?: false

    override fun setAuthenticated(isAuthenticated: Boolean) {
        initialAuth?.let { it.isAuthenticated = isAuthenticated }
    }
}

fun Authentication.enriched(): EnrichedAuthentication = this as EnrichedAuthentication
