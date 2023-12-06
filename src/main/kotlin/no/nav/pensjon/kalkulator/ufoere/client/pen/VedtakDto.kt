package no.nav.pensjon.kalkulator.ufoere.client.pen

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.ufoere.Sakstype
import org.springframework.util.StringUtils.hasLength

data class VedtakDto(val sakstype: String)

enum class PenSakstype(val externalValue: String, val internalValue: Sakstype) {
    NONE("", Sakstype.NONE),
    UNKNOWN("?", Sakstype.UNKNOWN),
    UFOEREPENSJON("UFOREP", Sakstype.UFOEREPENSJON);

    companion object {
        private val values = entries.toTypedArray()
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown PEN sakstype '$externalValue'" } }
            else
                NONE
    }
}
