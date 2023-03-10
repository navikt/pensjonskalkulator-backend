package no.nav.pensjon.kalkulator.regler

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class ReglerConfiguration {

    @Bean("regler")
    fun objectMapper(): ObjectMapper {
        val mapper = jacksonObjectMapper().registerModule(localDateModule())
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
        return mapper.activateDefaultTyping(mapper.polymorphicTypeValidator)
    }

    private fun localDateModule(): SimpleModule {
        return SimpleModule().addDeserializer(LocalDate::class.java, LocalDateDeserializer())
    }
}
