package no.nav.pensjon.kalkulator.tech.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Configuration of object mapper used for serialization and deserialization of data in JSON format.
 */
@Configuration
class ObjectMapperConfiguration {

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper().also {
            it.enable(SerializationFeature.INDENT_OUTPUT)
            it.registerModule(JavaTimeModule())
            it.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }
}
