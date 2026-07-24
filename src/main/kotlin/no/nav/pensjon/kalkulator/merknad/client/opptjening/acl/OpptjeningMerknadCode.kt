package no.nav.pensjon.kalkulator.merknad.client.opptjening.acl

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.merknad.MerknadCode
import org.springframework.util.StringUtils

enum class OpptjeningMerknadCode(val externalValue: String, private val internalValue: MerknadCode) {

    AFP(externalValue = "AFP", internalValue = MerknadCode.AFP),
    REFORM(externalValue = "REFORM", internalValue = MerknadCode.REFORM),
    INGEN_OPPTJENING(externalValue = "INGEN_OPPTJENING", internalValue = MerknadCode.INGEN_OPPTJENING),
    UFOEREGRAD(externalValue = "UFOREGRAD", internalValue = MerknadCode.UFOEREGRAD),
    DAGPENGER(externalValue = "DAGPENGER", internalValue = MerknadCode.DAGPENGER),
    FOERSTEGANGSTJENESTE(externalValue = "FORSTEGANGSTJENESTE", internalValue = MerknadCode.FOERSTEGANGSTJENESTE),
    OMSORGSOPPTJENING(externalValue = "OMSORGSOPPTJENING", internalValue = MerknadCode.OMSORGSOPPTJENING),
    GRADERT_UTTAK(externalValue = "GRADERT_UTTAK", internalValue = MerknadCode.GRADERT_UTTAK),
    HELT_UTTAK(externalValue = "HELT_UTTAK", internalValue = MerknadCode.HELT_UTTAK),

    // Special values representing missing/unknown value:
    NONE(externalValue = "", internalValue = MerknadCode.NONE),
    UNKNOWN(externalValue = "?", internalValue = MerknadCode.UNKNOWN);

    companion object {
        private val log = KotlinLogging.logger {}

        fun internalValue(externalValue: String?): MerknadCode =
            fromExternalValue(externalValue).internalValue

        private fun fromExternalValue(value: String?) =
            entries.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (StringUtils.hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown merknad '$externalValue'" } }
            else
                NONE
    }
}