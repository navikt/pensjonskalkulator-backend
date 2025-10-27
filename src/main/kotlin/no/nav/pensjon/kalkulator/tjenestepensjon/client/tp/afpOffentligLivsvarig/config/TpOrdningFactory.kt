package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.afpOffentligLivsvarig.config

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.afpOffentligLivsvarig.common.TpOrdningBaseService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * Factory som lager Tjenestepensjon ordning service instanser basert på config.
 */
@Configuration
@EnableConfigurationProperties(TpOrdningProperties::class)
class TpOrdningFactory(
    private val properties: TpOrdningProperties,
    private val webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid
) {
    private val log = KotlinLogging.logger {}

    @Bean
    fun tpOrdningMap(): Map<String, TpOrdningBaseService> {
        val providers = mutableMapOf<String, TpOrdningBaseService>()

        properties.tilbydere.forEach { (key, config) ->
            val webClient = webClientBuilder.baseUrl(extractBaseUrl(config.url)).build()

            // Create generic provider instance - config.url contains the complete URL pattern
            val provider = TpOrdningBaseService(config, webClient, traceAid)

            providers[key] = provider
            log.info { "Registered AFP provider: ${config.name} (key: $key) with URL pattern: ${config.url}" }
        }

        return providers
    }

    private fun extractBaseUrl(url: String): String {
        val regex = Regex("(https?://[^/]+)")
        return regex.find(url)?.value ?: url
    }
}
