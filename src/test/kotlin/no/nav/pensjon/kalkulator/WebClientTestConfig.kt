package no.nav.pensjon.kalkulator

import no.nav.pensjon.kalkulator.tech.web.WebClientConfig
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient

@TestConfiguration
class WebClientTestConfig {

    @Bean
    fun webClientBuilder(): WebClient.Builder =
        WebClient.builder().also { WebClientConfig().customize(it) }
}
