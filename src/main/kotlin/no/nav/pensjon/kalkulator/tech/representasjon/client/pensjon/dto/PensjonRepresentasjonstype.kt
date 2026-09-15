package no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon.dto

import no.nav.pensjon.kalkulator.common.api.EnumUtil.handleMissingExternalValue
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjonstype

/**
 * The external value corresponds with Representasjonstype in pensjon-representasjon, ref.
 * github.com/navikt/pensjon-representasjon/src/main/kotlin/no/nav/pensjon/fullmakt/representasjon/model/Representasjonstype
 */
enum class PensjonRepresentasjonstype(val internalValue: Representasjonstype, val externalValue: String) {
    PENSJON_LES(
        internalValue = Representasjonstype.PENSJON_LES,
        externalValue = "PENSJON_LES"
    ),
    PENSJON_SKRIV(
        internalValue = Representasjonstype.PENSJON_SKRIV,
        externalValue = "PENSJON_SKRIV"
    ),
    VERGE_PENSJON_LES(
        internalValue = Representasjonstype.VERGE_PENSJON_LES,
        externalValue = "VERGE_PENSJON_LES"
    ),
    VERGE_PENSJON_SKRIV(
        internalValue = Representasjonstype.VERGE_PENSJON_SKRIV,
        externalValue = "VERGE_PENSJON_SKRIV"
    );

    companion object {
        val valuesByInternal = entries.associateBy { it.internalValue }

        fun transferable(value: Representasjonstype?): String =
            from(value).externalValue

        private fun from(value: Representasjonstype?): PensjonRepresentasjonstype =
            value?.let { valuesByInternal[it] }
                ?: handleMissingExternalValue(apiId = "representasjon", type = "representasjonstype", value)
    }
}