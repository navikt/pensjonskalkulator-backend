package no.nav.pensjon.kalkulator.opptjening.client.popp.acl

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.opptjening.PensjonspoengType

/**
 * External values are defined by PensjonspoengTypeCode in POPP:
 * github.com/navikt/popp/blob/main/popp-domain/src/main/java/no/nav/popp/domain/codestable/PensjonspoengTypeCode.java
 */
enum class PoppPensjonspoengType(val externalValue: String, val internalValue: PensjonspoengType) {

    PENSJONSPOENG_FOR_PENSJONSGIVENDE_INNTEKT(
        externalValue = "PPI",
        internalValue = PensjonspoengType.PENSJONSPOENG_FOR_PENSJONSGIVENDE_INNTEKT
    ),
    OMSORG_FOR_SYKE_FUNKSJONSHEMMEDE_ELDRE(
        externalValue = "OSFE",
        internalValue = PensjonspoengType.OMSORG_FOR_SYKE_FUNKSJONSHEMMEDE_ELDRE
    ),
    OMSORG_FOR_BARN_OVER_7_AAR_MED_HJELPESTOENAD_SATS_3_ELLER_4(
        externalValue = "OBO7H",
        internalValue = PensjonspoengType.OMSORG_FOR_BARN_OVER_7_AAR_MED_HJELPESTOENAD_SATS_3_ELLER_4
    ),
    OMSORG_FOR_BARN_UNDER_7_AAR_EGET_VEDTAK(
        externalValue = "OBU7",
        internalValue = PensjonspoengType.OMSORG_FOR_BARN_UNDER_7_AAR_EGET_VEDTAK
    ),
    OMSORG_FOR_BARN_OVER_6_AAR_MED_HJELPESTOENAD_SATS_3_ELLER_4(
        externalValue = "OBO6H",
        internalValue = PensjonspoengType.OMSORG_FOR_BARN_OVER_6_AAR_MED_HJELPESTOENAD_SATS_3_ELLER_4
    ),
    OMSORG_FOR_BARN_UNDER_6_AAR_EGET_VEDTAK(
        externalValue = "OBU6",
        internalValue = PensjonspoengType.OMSORG_FOR_BARN_UNDER_6_AAR_EGET_VEDTAK
    ),

    // Default-verdi som representerer uventede/manglende verdier fra POPP:
    UNKNOWN(externalValue = "?", internalValue = PensjonspoengType.UNKNOWN);

    companion object {
        private val valuesByExternal = entries.associateBy { it.externalValue }
        private val log = KotlinLogging.logger {}

        fun toInternalValue(externalValue: String?): PensjonspoengType =
            externalValue?.let { valuesByExternal[it]?.internalValue }
                ?: PensjonspoengType.UNKNOWN.also { log.warn { "ukjent pensjonspoengtype fra POPP '$externalValue'" } }
    }
}