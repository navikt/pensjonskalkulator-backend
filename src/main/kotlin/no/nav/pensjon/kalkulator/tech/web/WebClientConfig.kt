package no.nav.pensjon.kalkulator.tech.web

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
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
    fun regularWebClient(): WebClient =
        WebClient.builder()
            .filter(filterResponse())
            .build()

    @Bean
    @Qualifier("large-response")
    fun largeBufferWebClient(): WebClient =
        WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(HttpClient.create()))
            .exchangeStrategies(largeBufferStrategies())
            .filter(filterResponse())
            .build()

    @Bean
    @Qualifier("soap")
    fun webClientForSoapRequests(): WebClient =
        WebClient.builder()
            .defaultHeaders { it.contentType = MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8) }
            .filter(filterResponse())
            .build()

    companion object {
        private const val MAX_IN_MEMORY_SIZE = 10485760 // 10 MB (10 * 1024 * 1024)

        private fun largeBufferStrategies(): ExchangeStrategies =
            ExchangeStrategies.builder()
                .codecs { it.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE) }
                .build()

        private fun filterResponse() = ExchangeFilterFunction.ofResponseProcessor { filter(it) }

        private fun filter(response: ClientResponse): Mono<ClientResponse> {
            val statusCode = response.statusCode()

            return if (statusCode.is4xxClientError)
                clientError(statusCode, response)
            else if (statusCode.is5xxServerError)
                serverError(response)
            else
                Mono.just(response)
        }

        private fun clientError(statusCode: HttpStatusCode, response: ClientResponse): Mono<ClientResponse> =
            response
                .bodyToMono(String::class.java)
                .defaultIfEmpty(emptyResponseInfo(response))
                .flatMap { Mono.error(EgressException(message = it, statusCode = statusCode)) }

        private fun serverError(response: ClientResponse): Mono<ClientResponse> =
            response
                .bodyToMono(String::class.java)
                .flatMap { Mono.error(EgressException(message = it)) }

        private fun emptyResponseInfo(response: ClientResponse): String {
            val statusCode = response.statusCode()

            return if (isAccessDenied(statusCode))
                "$statusCode: ${reasonForAccessDenial(response)}"
            else
                statusCode.toString()
        }

        private fun isAccessDenied(statusCode: HttpStatusCode) =
            statusCode == HttpStatus.FORBIDDEN || statusCode == HttpStatus.UNAUTHORIZED

        private fun reasonForAccessDenial(response: ClientResponse): String =
            response.headers().asHttpHeaders()[HttpHeaders.WWW_AUTHENTICATE]?.firstOrNull() ?: "(access denied)"
    }
}
