package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.EksternBeregningsmodell
import org.springframework.util.StringUtils.hasLength

enum class Beregningsmodell(val externalValue: String, val internalValue: EksternBeregningsmodell) {
    NONE("", EksternBeregningsmodell.NONE),
    UNKNOWN("?", EksternBeregningsmodell.UNKNOWN),
    BRANSJEAVTALE("bransjeavtale", EksternBeregningsmodell.BRANSJEAVTALE), // prognosen er levert av pensjonsleverandør
    NORSK_PENSJON("norskpensjon", EksternBeregningsmodell.NORSK_PENSJON); // prognosen er beregnet av Norsk Pensjon

    companion object {
        private val values = values()
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown NP beregningsmodell '$externalValue'" } }
            else
                NONE
    }
}
