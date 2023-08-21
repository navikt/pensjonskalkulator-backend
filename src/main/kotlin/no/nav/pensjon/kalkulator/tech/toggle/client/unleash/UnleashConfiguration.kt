package no.nav.pensjon.kalkulator.tech.toggle.client.unleash

import io.getunleash.DefaultUnleash
import io.getunleash.util.UnleashConfig
import no.nav.pensjon.kalkulator.tech.env.NaisEnvironment
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UnleashConfiguration(
    @Value("\${unleash.server.api.url}") private val endpoint: String,
    @Value("\${unleash.server.api.token}") private val apiKey: String,
    @Value("\${unleash.toggle.interval}") private val toggleInterval: String,
    @Value("\${nais.cluster.name}") private val clusterName: String
) {
    @Bean
    fun unleashConfig(@Value("\${nais.app.name}") appName: String) =
        UnleashConfig.builder()
            .appName(appName)
            .environment(environment())
            .instanceId(instanceId())
            .fetchTogglesInterval(toggleInterval.toLong())
            .unleashAPI("$endpoint/api")
            .apiKey(apiKey)
            .build()

    @Bean
    fun defaultUnleash(config: UnleashConfig) = DefaultUnleash(config)

    private fun environment() =
        if (clusterName == NaisEnvironment.PRODUCTION_CLUSTER_NAME)
            PRODUCTION_ENVIRONMENT_NAME
        else
            DEFAULT_ENVIRONMENT_NAME

    private fun instanceId() = System.getProperty("instance.id") ?: DEFAULT_INSTANCE_ID

    private companion object {
        private const val PRODUCTION_ENVIRONMENT_NAME = "p"
        private const val DEFAULT_ENVIRONMENT_NAME = "q2"
        private const val DEFAULT_INSTANCE_ID = "local"
    }
}
