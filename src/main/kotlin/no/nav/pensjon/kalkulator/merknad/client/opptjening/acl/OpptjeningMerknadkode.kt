package no.nav.pensjon.kalkulator.merknad.client.opptjening.acl

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.merknad.MerknadCode
import org.springframework.util.StringUtils.hasLength

/**
 * Definerer mapping mellom ekstern og intern representasjon av merknadskoder
 * relatert til opptjening.
 * – Intern verdi er den som er brukt i denne appens domene
 * – Ekstern verdi er definert i MerknadCodeV2 i pensjon-selvbetjening-opptjening-backend
 * Ref. github.com/navikt/pensjon-selvbetjening-opptjening-backend/.../api/merknad/v2/acl/MerknaderV2.kt
 */
enum class OpptjeningMerknadkode(
    private val externalValue: String,
    private val internalValue: MerknadCode
) {
    AFP(
        externalValue = "AFP",
        internalValue = MerknadCode.AFP
    ),
    REFORM(
        externalValue = "REFORM",
        internalValue = MerknadCode.REFORM
    ),
    INGEN_OPPTJENING(
        externalValue = "INGEN_OPPTJENING",
        internalValue = MerknadCode.INGEN_OPPTJENING
    ),
    UFOEREGRAD(
        externalValue = "UFOREGRAD",
        internalValue = MerknadCode.UFOEREGRAD
    ),
    DAGPENGER(
        externalValue = "DAGPENGER",
        internalValue = MerknadCode.DAGPENGER
    ),
    FOERSTEGANGSTJENESTE(
        externalValue = "FORSTEGANGSTJENESTE",
        internalValue = MerknadCode.FOERSTEGANGSTJENESTE
    ),
    OMSORGSOPPTJENING(
        externalValue = "OMSORGSOPPTJENING",
        internalValue = MerknadCode.OMSORGSOPPTJENING
    ),
    GRADERT_UTTAK(
        externalValue = "GRADERT_UTTAK",
        internalValue = MerknadCode.GRADERT_UTTAK
    ),
    HELT_UTTAK(
        externalValue = "HELT_UTTAK",
        internalValue = MerknadCode.HELT_UTTAK
    ),
    UTTAK_STOPPET(
        externalValue = "UTTAK_STOPPET",
        internalValue = MerknadCode.UTTAK_STOPPET
    ),

    // Special values representing missing/unknown value:
    NONE(
        externalValue = "",
        internalValue = MerknadCode.NONE
    ),
    UNKNOWN(
        externalValue = "?",
        internalValue = MerknadCode.UNKNOWN
    );

    companion object {
        private val log = KotlinLogging.logger {}
        private val valuesByExternal = entries.associateBy { it.externalValue }

        fun internalValue(externalValue: String?): MerknadCode =
            fromExternalValue(externalValue).internalValue

        private fun fromExternalValue(value: String?) =
            value?.let { valuesByExternal[it] } ?: default(externalValue = value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown merknad '$externalValue'" } }
            else
                NONE
    }
}