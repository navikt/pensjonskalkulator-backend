package no.nav.pensjon.kalkulator.opptjening.client.popp.dto

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.opptjening.Beholdningsoppdateringsaarsak
import org.springframework.util.StringUtils.hasLength
import java.util.*

/**
 * Data transfer object for the result of a request to the pensjon-popp beholdning API.
 * Field names are dictated by pensjon-popp.
 */
data class PoppBeholdningResult(
    val beholdninger: List<PoppBeholdning>
)

data class PoppBeholdning(
    val belop: Double,
    val oppdateringArsak: String,
    val fomDato: Date
)

enum class PoppBeholdningsoppdateringsaarsak(val externalValue: String, val internalValue: Beholdningsoppdateringsaarsak) {
    NY_OPPTJENING(
        externalValue = "NY_OPPTJENING",
        internalValue = Beholdningsoppdateringsaarsak.NY_OPPTJENING
    ),
    REGULERING(
        externalValue = "REGULERING",
        internalValue = Beholdningsoppdateringsaarsak.REGULERING
    ),
    VEDTAK(
        externalValue = "VEDTAK",
        internalValue = Beholdningsoppdateringsaarsak.VEDTAK
    ),

    // Special value not used by POPP (for handling unexpected/missing enum values):
    UNKNOWN(externalValue = "?", internalValue = Beholdningsoppdateringsaarsak.UNKNOWN);

    companion object {
        private val log = KotlinLogging.logger {}

        val valuesByExternal = entries.associateBy { it.externalValue }

        fun internalValue(value: String?): Beholdningsoppdateringsaarsak =
            fromExternalValue(value).internalValue

        private fun fromExternalValue(value: String?): PoppBeholdningsoppdateringsaarsak =
            value?.let { valuesByExternal[it.uppercase()] } ?: default(value)

        private fun default(externalValue: String?): PoppBeholdningsoppdateringsaarsak =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Ukjent ekstern beholdningsoppdateringsårsak '$externalValue'" } }
            else
                UNKNOWN
    }
}