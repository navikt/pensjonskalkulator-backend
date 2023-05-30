package no.nav.pensjon.kalkulator.tech.toggle.client.unleash

import io.getunleash.strategy.Strategy
import no.nav.pensjon.kalkulator.tech.env.NaisEnvironment


class IsNotProductionStrategy(clusterName: String) : Strategy {

    private val isProductionEnvironment = clusterName == NaisEnvironment.PRODUCTION_CLUSTER_NAME

    override fun getName() = STRATEGY_NAME

    override fun isEnabled(map: MutableMap<String, String>) = !isProductionEnvironment

    private companion object {
        private const val STRATEGY_NAME = "isNotProd"
    }
}
