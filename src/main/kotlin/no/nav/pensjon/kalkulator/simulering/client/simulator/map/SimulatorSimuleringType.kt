package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.simulering.SimuleringType

/**
 * The 'externalValue' is simulering-type values used by pensjonssimulator.
 * The original source of the external values is PEN:
 * https://github.com/navikt/pesys/blob/main/pen/domain/nav-domain-pensjon-pen-api/src/main/java/no/nav/domain/pensjon/kjerne/kodetabeller/SimuleringTypeCode.java
 */
enum class SimulatorSimuleringType(
    val externalValue: String,
    val internalValue: SimuleringType
) {
    ALDERSPENSJON(
        externalValue = "ALDER",
        internalValue = SimuleringType.ALDERSPENSJON
    ),
    PRE2025_OFFENTLIG_AFP_ETTERFULGT_AV_ALDERSPENSJON(
        externalValue = "AFP_ETTERF_ALDER",
        internalValue = SimuleringType.PRE2025_OFFENTLIG_AFP_ETTERFULGT_AV_ALDERSPENSJON
    ),
    ALDERSPENSJON_MED_AFP_PRIVAT(
        externalValue = "ALDER_M_AFP_PRIVAT",
        internalValue = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT
    ),
    ALDERSPENSJON_MED_LIVSVARIG_OFFENTLIG_AFP(
        externalValue = "ALDERSPENSJON_MED_LIVSVARIG_OFFENTLIG_AFP",
        internalValue = SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG
    ),
    ALDERSPENSJON_MED_GJENLEVENDERETT(
        externalValue = "ALDER_M_GJEN",
        internalValue = SimuleringType.ALDERSPENSJON_MED_GJENLEVENDERETT
    ),
    ENDRING_ALDERSPENSJON(
        externalValue = "ENDR_ALDER",
        internalValue = SimuleringType.ENDRING_ALDERSPENSJON
    ),
    ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT(
        externalValue = "ENDR_AP_M_AFP_PRIVAT",
        internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT
    ),
    ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG(
        externalValue = "ENDR_AP_M_AFP_OFFENTLIG_LIVSVARIG",
        internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG
    ),
    ENDRING_ALDERSPENSJON_MED_GJENLEVENDERETT(
        externalValue = "ENDR_ALDER_M_GJEN",
        internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_GJENLEVENDERETT
    );

    companion object {
        private val values = entries.toTypedArray()

        fun fromInternalValue(value: SimuleringType) =
            values.singleOrNull { it.internalValue == value } ?: ALDERSPENSJON
    }
}
