package no.nav.pensjon.kalkulator

import no.nav.pensjon.kalkulator.tech.web.WebClientConfig
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.codec.json.JacksonJsonDecoder
import org.springframework.http.codec.json.JacksonJsonEncoder
import org.springframework.web.reactive.function.client.WebClient
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper

@TestConfiguration
class WebClientTestConfig {

    @Bean
    fun webClientBuilder(): WebClient.Builder =
        WebClient.builder()
            .codecs {
                it.defaultCodecs().jacksonJsonDecoder(JacksonJsonDecoder(deserializationMapper()))
                it.defaultCodecs().jacksonJsonEncoder(JacksonJsonEncoder(JsonMapper.builder()))
            }.also { WebClientConfig().customize(it) }

    private companion object {
        private fun deserializationMapper(): JsonMapper =
            JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build()
    }
}
