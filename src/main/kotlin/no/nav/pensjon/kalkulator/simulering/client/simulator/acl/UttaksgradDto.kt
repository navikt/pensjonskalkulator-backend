package no.nav.pensjon.kalkulator.simulering.client.simulator.acl

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import org.springframework.util.StringUtils.hasLength

/**
 * Corresponds to the following class in pensjonssimulator:
 * no.nav.pensjon.simulator.api.nav.v1.acl.UttaksgradDto
 */
enum class UttaksgradDto(val internalValue: Uttaksgrad) {
    NULL(internalValue = Uttaksgrad.NULL),
    TJUE_PROSENT(internalValue = Uttaksgrad.TJUE_PROSENT),
    FOERTI_PROSENT(internalValue = Uttaksgrad.FOERTI_PROSENT),
    FEMTI_PROSENT(internalValue = Uttaksgrad.FEMTI_PROSENT),
    SEKSTI_PROSENT(internalValue = Uttaksgrad.SEKSTI_PROSENT),
    AATTI_PROSENT(internalValue = Uttaksgrad.AATTI_PROSENT),
    HUNDRE_PROSENT(internalValue = Uttaksgrad.HUNDRE_PROSENT);

    companion object {
        private val values = UttaksgradDto.entries.toTypedArray()
        private val defaultValue = NULL
        private val log = KotlinLogging.logger {}

        fun internalValue(value: String?) =
            fromExternalValue(value).internalValue

        fun fromInternalValue(value: Uttaksgrad): UttaksgradDto =
            values.singleOrNull { it.internalValue == value } ?: NULL

        private fun fromExternalValue(value: String?) =
            values.singleOrNull { it.name.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                defaultValue.also { log.warn { "Ekstern verdi ikke støttet - pensjonssimulator returnerte uttaksgrad '$externalValue'" } }
            else
                defaultValue
    }
}
