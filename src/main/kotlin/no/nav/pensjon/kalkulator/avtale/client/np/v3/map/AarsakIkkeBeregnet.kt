package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.ManglendeEksternBeregningAarsak
import org.springframework.util.StringUtils.hasLength

enum class AarsakIkkeBeregnet(val externalValue: String, val internalValue: ManglendeEksternBeregningAarsak) {

    NONE("", ManglendeEksternBeregningAarsak.NONE),
    UNKNOWN("?", ManglendeEksternBeregningAarsak.UNKNOWN),

    // Rettigheten har en ugyldig kombinasjon av hovedkategori og underkategori, og kunne ikke beregnes pga. dette.
    UKJENT_PRODUKTTYPE("UKJENT_PRODUKTTYPE", ManglendeEksternBeregningAarsak.UKJENT_PRODUKTTYPE),

    UTILSTREKKELIG_DATA("UTILSTREKKELIG_DATA", ManglendeEksternBeregningAarsak.UTILSTREKKELIG_DATA);

    companion object {
        private val values = entries.toTypedArray()
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown NP årsak ikke beregnet '$externalValue'" } }
            else
                NONE
    }
}
