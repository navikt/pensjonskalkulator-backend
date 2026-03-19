package no.nav.pensjon.kalkulator.avtale.client.np.v3.dto

import mu.KotlinLogging
import org.springframework.util.StringUtils.hasLength
import no.nav.pensjon.kalkulator.person.Sivilstatus as InternalSivilstatus

enum class Sivilstatus(val externalValue: String, val internalValue: InternalSivilstatus) {
    NONE(externalValue = "", internalValue = InternalSivilstatus.UOPPGITT),
    UNKNOWN(externalValue = "?", internalValue = InternalSivilstatus.UNKNOWN),
    GIFT(externalValue = "gift", internalValue = InternalSivilstatus.GIFT),
    UGIFT(externalValue = "ugift", internalValue = InternalSivilstatus.UGIFT);

    companion object {
        private val defaultValue = GIFT // Norsk Pensjon default
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            entries.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        fun fromInternalValue(value: InternalSivilstatus?) =
            entries.singleOrNull { it.internalValue == value } ?: defaultValue

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown NP sivilstatus '$externalValue'" } }
            else
                NONE
    }
}
