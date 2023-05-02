package no.nav.pensjon.kalkulator.tech.security.egress

import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class SecurityContextEnricherTest {

    @Test
    fun `enrichAuthentication tolerates null authentication`() {
        SecurityContextHolder.setContext(SecurityContextImpl(null))
        assertDoesNotThrow { SecurityContextEnricher(tokenSuppliers()).enrichAuthentication() }
    }

    private companion object {
        private fun tokenSuppliers() = EgressTokenSuppliersByService(emptyMap())
    }
}
