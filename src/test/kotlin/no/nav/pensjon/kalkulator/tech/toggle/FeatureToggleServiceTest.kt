package no.nav.pensjon.kalkulator.tech.toggle

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.tech.toggle.client.FeatureToggleClient

class FeatureToggleServiceTest : ShouldSpec({

    should("return 'true' if feature is enabled") {
        FeatureToggleService(
            client = mockk<FeatureToggleClient>().apply {
                every { isEnabled(featureName = "feature1") } returns true
            }
        ).isEnabled("feature1") shouldBe true
    }
})
