package no.nav.pensjon.kalkulator.regler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ReglerConfiguration {

    @Bean
    fun objectMapper(): ObjectMapper {
        val mapper = jacksonObjectMapper()
        return mapper.activateDefaultTyping(mapper.polymorphicTypeValidator)
    }
}
