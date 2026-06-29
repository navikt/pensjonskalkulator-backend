package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.ManglendeEksternBeregningAarsak
import org.springframework.util.StringUtils.hasLength

enum class AarsakIkkeBeregnet(val externalValue: String, val internalValue: ManglendeEksternBeregningAarsak) {

    GENERELL_FEIL_MANGLENDE_PROGNOSE(
        externalValue = "GENERELL_FEIL_MANGLENDE_PROGNOSE",
        internalValue = ManglendeEksternBeregningAarsak.GENERELL_FEIL_MANGLENDE_PROGNOSE
    ),

    // Rettigheten har en ugyldig kombinasjon av hovedkategori og underkategori, og kunne ikke beregnes pga. dette.
    UKJENT_PRODUKTTYPE(
        externalValue = "UKJENT_PRODUKTTYPE",
        internalValue = ManglendeEksternBeregningAarsak.UKJENT_PRODUKTTYPE
    ),

    UTILSTREKKELIG_DATA(
        externalValue = "UTILSTREKKELIG_DATA",
        internalValue = ManglendeEksternBeregningAarsak.UTILSTREKKELIG_DATA
    ),

    // Special values not used by Norsk Pensjon (used for missing/unknown values):
    NONE(
        externalValue = "",
        internalValue = ManglendeEksternBeregningAarsak.NONE
    ),
    UNKNOWN(
        externalValue = "?",
        internalValue = ManglendeEksternBeregningAarsak.UNKNOWN
    );

    companion object {
        private val log = KotlinLogging.logger {}

        fun internalValue(externalValue: String?): ManglendeEksternBeregningAarsak =
            fromExternalValue(externalValue).internalValue

        private fun fromExternalValue(value: String?) =
            entries.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown NP årsak ikke beregnet '$externalValue'" } }
            else
                NONE
    }
}