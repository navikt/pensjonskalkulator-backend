package no.nav.pensjon.kalkulator.regler

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class LocalDateDeserializer : StdDeserializer<LocalDate?>(LocalDate::class.java) {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): LocalDate {
        return Instant.ofEpochMilli(parser.readValueAs(Long::class.java))
            .atZone(ZoneId.of(ZONE_ID))
            .toLocalDate()
    }

    companion object {
        private const val ZONE_ID = "UTC+2" // Norway daylight saving time
    }
}
