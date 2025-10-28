package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.config

import mu.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Factory som lager Tjenestepensjon ordning config map basert på properties.
 */
@Configuration
@EnableConfigurationProperties(TpOrdningProperties::class)
class TpOrdningFactory(
    private val properties: TpOrdningProperties
) {
    private val log = KotlinLogging.logger {}

    @Bean
    fun afpOrdningConfigMap(): Map<String, TpOrdningConfig> {
        val configs = mutableMapOf<String, TpOrdningConfig>()

        properties.tilbydere.forEach { (key, config) ->
            configs[key] = config
            log.info { "Registered AFP provider config: ${config.name} (key: $key) with URL pattern: ${config.url}" }
        }

        return configs
    }
}
