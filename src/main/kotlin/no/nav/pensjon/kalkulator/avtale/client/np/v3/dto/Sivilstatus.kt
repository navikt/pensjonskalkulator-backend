package no.nav.pensjon.kalkulator.avtale.client.np.v3.dto

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Sivilstand
import org.springframework.util.StringUtils

enum class Sivilstatus(val externalValue: String, val internalValue: Sivilstand) {
    NONE("", Sivilstand.UOPPGITT),
    UNKNOWN("?", Sivilstand.UNKNOWN),
    GIFT("gift", Sivilstand.GIFT),
    UGIFT("ugift", Sivilstand.UGIFT);

    companion object {
        private val values = Sivilstatus.values()
        private val defaultValue = GIFT // Norsk Pensjon default
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) }
                ?: default(value).also { log.warn { "Unknown NP sivilstatus '$value'" } }

        fun fromInternalValue(value: Sivilstand?) =
            values.singleOrNull { it.internalValue == value } ?: defaultValue

        private fun default(externalValue: String?) = if (StringUtils.hasLength(externalValue)) UNKNOWN else NONE
    }
}
