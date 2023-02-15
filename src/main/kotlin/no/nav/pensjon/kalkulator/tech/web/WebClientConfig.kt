package no.nav.pensjon.kalkulator.tech.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfig {

    @Bean
    fun largeBufferWebClient(): WebClient {
        val httpClient = HttpClient.create().wiretap(true)

        val strategies = ExchangeStrategies.builder()
            .codecs { it.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE) }
            .build()

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(strategies).build()
    }

    companion object{
        private const val MAX_IN_MEMORY_SIZE = 10485760 // 10 MB (10 * 1024 * 1024)
    }
}
