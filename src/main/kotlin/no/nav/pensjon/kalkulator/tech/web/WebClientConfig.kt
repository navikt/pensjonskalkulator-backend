package no.nav.pensjon.kalkulator.tech.web

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import java.nio.charset.StandardCharsets


@Configuration
class WebClientConfig {

    @Bean
    @Primary
    fun largeBufferWebClient(): WebClient {
        val httpClient = HttpClient.create().wiretap(true)

        val strategies = ExchangeStrategies.builder()
            .codecs { it.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE) }
            .build()

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(strategies).build()
    }

    @Bean
    @Qualifier("soap")
    fun webClientForSoapRequests(): WebClient {
        return WebClient.builder()
            .defaultHeaders { it.contentType = MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8) }
            .filter(filterResponse())
            .build()
    }

    companion object {
        private const val MAX_IN_MEMORY_SIZE = 10485760 // 10 MB (10 * 1024 * 1024)

        private fun filterResponse() = ExchangeFilterFunction.ofResponseProcessor { filter(it) }

        private fun filter(response: ClientResponse): Mono<ClientResponse> {
            val statusCode = response.statusCode()

            return if (statusCode.is4xxClientError) {
                response.bodyToMono(String::class.java).flatMap { Mono.error(EgressException(true, it)) }
            } else if (statusCode.is5xxServerError) {
                response.bodyToMono(String::class.java).flatMap { Mono.error(EgressException(false, it)) }
            } else {
                Mono.just(response)
            }
        }
    }
}
