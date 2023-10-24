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
        private val values = PenSakstype.values()
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) }
                ?: default(value).also { log.warn { "Unknown PEN sakstype '$value'" } }

        private fun default(externalValue: String?) = if (hasLength(externalValue)) UNKNOWN else NONE
    }
}
