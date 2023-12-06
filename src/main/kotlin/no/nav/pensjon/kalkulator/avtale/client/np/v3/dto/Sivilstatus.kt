package no.nav.pensjon.kalkulator.avtale.client.np.v3.dto

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Sivilstand
import org.springframework.util.StringUtils
import org.springframework.util.StringUtils.hasLength

enum class Sivilstatus(val externalValue: String, val internalValue: Sivilstand) {
    NONE("", Sivilstand.UOPPGITT),
    UNKNOWN("?", Sivilstand.UNKNOWN),
    GIFT("gift", Sivilstand.GIFT),
    UGIFT("ugift", Sivilstand.UGIFT);

    companion object {
        private val values = entries.toTypedArray()
        private val defaultValue = GIFT // Norsk Pensjon default
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        fun fromInternalValue(value: Sivilstand?) =
            values.singleOrNull { it.internalValue == value } ?: defaultValue

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown NP sivilstatus '$externalValue'" } }
            else
                NONE
    }
}
