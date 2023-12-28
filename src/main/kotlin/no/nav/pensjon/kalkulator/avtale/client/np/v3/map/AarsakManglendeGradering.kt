package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.ManglendeEksternGraderingAarsak
import org.springframework.util.StringUtils.hasLength

enum class AarsakManglendeGradering(val externalValue: String, val internalValue: ManglendeEksternGraderingAarsak) {

    NONE("", ManglendeEksternGraderingAarsak.NONE),
    UNKNOWN("?", ManglendeEksternGraderingAarsak.UNKNOWN),

    // Hvis rettigheten normalt skal kunne graderes, men innretningen ikke kunne levere gradert data.
    // Eller hvis fleksibel startdato ikke støttes.
    IKKE_STOTTET("IKKE_STOTTET", ManglendeEksternGraderingAarsak.IKKE_STOETTET),

    // Hvis regelverket ikke tillater gradering av denne type rettighet.
    IKKE_TILLATT("IKKE_TILLATT", ManglendeEksternGraderingAarsak.IKKE_TILLATT);

    companion object {
        private val values = entries.toTypedArray()
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown NP årsak manglende gradering '$externalValue'" } }
            else
                NONE
    }
}
