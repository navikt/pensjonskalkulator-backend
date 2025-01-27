package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.simulering.SimuleringType

/**
 * The 'externalValue' is simulering-type values used by pensjonssimulator.
 * The original source of the external values is PEN:
 * https://github.com/navikt/pesys/blob/main/pen/domain/nav-domain-pensjon-pen-api/src/main/java/no/nav/domain/pensjon/kjerne/kodetabeller/SimuleringTypeCode.java
 */
enum class SimulatorSimuleringType(val externalValue: String, val internalValue: SimuleringType) {

    ALDERSPENSJON("ALDER", SimuleringType.ALDERSPENSJON),
    ALDERSPENSJON_MED_AFP_PRIVAT("ALDER_M_AFP_PRIVAT", SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT),
    ALDERSPENSJON_MED_LIVSVARIG_OFFENTLIG_AFP(externalValue = "ALDERSPENSJON_MED_LIVSVARIG_OFFENTLIG_AFP", internalValue = SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG),
    ENDRING_ALDERSPENSJON("ENDR_ALDER", SimuleringType.ENDRING_ALDERSPENSJON),
    ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT("ENDR_AP_M_AFP_PRIVAT", SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT),
    ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG("ENDR_AP_M_AFP_OFFENTLIG_LIVSVARIG", SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG);

    companion object {
        private val values = entries.toTypedArray()

        fun fromInternalValue(value: SimuleringType) =
            values.singleOrNull { it.internalValue == value } ?: ALDERSPENSJON
    }
}
