package no.nav.pensjon.kalkulator.opptjening.api.v2.acl

import no.nav.pensjon.kalkulator.merknad.MerknadCode

enum class MerknadCodeV2(private val internalValue: MerknadCode) {

    AFP(internalValue = MerknadCode.AFP),
    REFORM(internalValue = MerknadCode.REFORM),
    INGEN_OPPTJENING(internalValue = MerknadCode.INGEN_OPPTJENING),
    UFOEREGRAD(internalValue = MerknadCode.UFOEREGRAD),
    DAGPENGER(internalValue = MerknadCode.DAGPENGER),
    FOERSTEGANGSTJENESTE(internalValue = MerknadCode.FOERSTEGANGSTJENESTE),
    OMSORGSOPPTJENING(internalValue = MerknadCode.OMSORGSOPPTJENING),
    GRADERT_UTTAK(internalValue = MerknadCode.GRADERT_UTTAK),
    HELT_UTTAK(internalValue = MerknadCode.HELT_UTTAK),
    UTTAK_STOPPET(internalValue = MerknadCode.UTTAK_STOPPET),

    // Special value representing missing/unknown value:
    UNKNOWN(internalValue = MerknadCode.UNKNOWN),
    NONE(internalValue = MerknadCode.NONE);

    companion object {
        private val valuesByInternal = entries.associateBy { it.internalValue }

        fun transferable(value: MerknadCode): MerknadCodeV2 =
            valuesByInternal[value]
                ?: throw IllegalArgumentException("Intern verdi ikke støttet i API v2 - MerknadCode $value")
    }
}