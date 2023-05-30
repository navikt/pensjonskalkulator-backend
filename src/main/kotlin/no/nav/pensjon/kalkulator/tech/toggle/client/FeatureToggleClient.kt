package no.nav.pensjon.kalkulator.tech.toggle.client

interface FeatureToggleClient {

    fun isEnabled(featureName: String): Boolean
}
