package no.nav.pensjon.kalkulator.person

import no.nav.pensjon.kalkulator.tech.security.egress.enriched
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/**
 * Determines whether full name is required.
 */
@Component
class NavnRequirement {

    /**
     * Fullt navn required in 'on behalf' context (veileder etc.)
     */
    fun needFulltNavn(): Boolean =
        SecurityContextHolder.getContext().authentication?.enriched()?.isOnBehalf ?: false
}
