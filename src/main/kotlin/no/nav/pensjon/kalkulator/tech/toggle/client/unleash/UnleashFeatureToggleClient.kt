package no.nav.pensjon.kalkulator.tech.toggle.client.unleash

import io.getunleash.Unleash
import no.nav.pensjon.kalkulator.tech.toggle.client.FeatureToggleClient
import org.springframework.stereotype.Component

@Component
class UnleashFeatureToggleClient(private val unleash: Unleash) : FeatureToggleClient {

    override fun isEnabled(featureName: String) = unleash.isEnabled(featureName)
}
