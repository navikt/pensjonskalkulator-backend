package no.nav.pensjon.kalkulator.tech.toggle

import no.nav.pensjon.kalkulator.person.*
import no.nav.pensjon.kalkulator.tech.toggle.client.FeatureToggleClient
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class FeatureToggleServiceTest {

    @Mock
    private lateinit var client: FeatureToggleClient

    @Test
    fun isEnabled() {
        `when`(client.isEnabled(FEATURE_NAME)).thenReturn(true)
        val enabled = FeatureToggleService(client).isEnabled(FEATURE_NAME)
        assertTrue(enabled)
    }

    private companion object {
        private const val FEATURE_NAME = "feature1"
    }
}
