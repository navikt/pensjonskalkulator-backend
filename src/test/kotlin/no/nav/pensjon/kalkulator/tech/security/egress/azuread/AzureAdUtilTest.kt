package no.nav.pensjon.kalkulator.tech.security.egress.azuread

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AzureAdUtilTest {

    @Test
    fun `getDefaultScope returns the default scope for a given service`() {
        val scope = AzureAdUtil.getDefaultScope("cluster1:namespace1:app1")
        assertEquals("api://cluster1.namespace1.app1/.default", scope)
    }
}
