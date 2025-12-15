package no.nav.pensjon.kalkulator.tech.json

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.module.SimpleModule
import tools.jackson.databind.ser.std.StdSerializer
import tools.jackson.dataformat.xml.XmlMapper
import java.util.*

/**
 * Configuration of object mappers used for serialization and deserialization of data in JSON format.
 */
@Configuration
class ObjectMapperConfiguration {

    @Bean
    @Primary
    fun objectMapper(): JsonMapper =
        JsonMapper.builder()
            .addModule(dateSerializerModule())
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .changeDefaultPropertyInclusion { it.withValueInclusion(NON_NULL) }
            .build()

    @Bean
    fun xmlMapper(): XmlMapper =
        XmlMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build()

    companion object {
        fun dateSerializerModule() =
            SimpleModule().apply {
                addSerializer(EpochDateSerializer(handledType = Date::class.java))
            }
    }
}

/**
 * Serializes java.util.Date as epoch milliseconds.
 * Used for calls to pensjon-regler.
 */
class EpochDateSerializer(handledType: Class<Date>) : StdSerializer<Date>(handledType) {

    override fun serialize(value: Date, gen: JsonGenerator, provider: SerializationContext) {
        gen.writeNumber(value.toInstant().toEpochMilli())
    }
}