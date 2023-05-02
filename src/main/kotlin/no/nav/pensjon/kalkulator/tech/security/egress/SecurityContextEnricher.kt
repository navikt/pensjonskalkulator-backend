package no.nav.pensjon.kalkulator.tech.security.egress

import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityContextEnricher(val tokenSuppliers: EgressTokenSuppliersByService) {

    fun enrichAuthentication() {
        val securityContext = SecurityContextHolder.getContext()

        securityContext.authentication =
            securityContext.authentication?.let { EnrichedAuthentication(it, tokenSuppliers) }
    }
}
