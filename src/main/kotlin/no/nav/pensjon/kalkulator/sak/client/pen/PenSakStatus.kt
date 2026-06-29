package no.nav.pensjon.kalkulator.sak.client.pen

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.sak.SakStatus
import org.springframework.util.StringUtils.hasLength

/**
 * The 'externalValue' corresponds with
 * no.nav.domain.pensjon.kjerne.kodetabeller.SakStatusCode
 * in pensjon-pen
 */
enum class PenSakStatus(val externalValue: String, val internalValue: SakStatus) {
    OPPRETTET(
        externalValue = "OPPRETTET",
        internalValue = SakStatus.OPPRETTET
    ),
    TIL_BEHANDLING(
        externalValue = "TIL_BEHANDLING",
        internalValue = SakStatus.TIL_BEHANDLING
    ),
    LOEPENDE(
        externalValue = "LOPENDE",
        internalValue = SakStatus.LOEPENDE
    ),
    AVSLUTTET(
        externalValue = "AVSLUTTET",
        internalValue = SakStatus.AVSLUTTET
    ),
    // Special values not in pensjon-pen (used for missing/unknown values):
    NONE(
        externalValue = "",
        internalValue = SakStatus.NONE
    ),
    UNKNOWN(
        externalValue = "?",
        internalValue = SakStatus.UNKNOWN
    );

    companion object {
        private val log = KotlinLogging.logger {}

        fun internalValue(externalValue: String?): SakStatus =
            fromExternalValue(externalValue).internalValue

        private fun fromExternalValue(value: String?) =
            entries.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown PEN sak-status '$externalValue'" } }
            else
                NONE
    }
}