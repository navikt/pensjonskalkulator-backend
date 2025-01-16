package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.simulering.SimuleringType

/**
 * The 'externalValue' is simulering-type values used by PEN (pensjonsfaglig kjerne).
 * The source of PEN's simulering-type values is:
 * https://github.com/navikt/pesys/blob/main/pen/domain/nav-domain-pensjon-pen-api/src/main/java/no/nav/domain/pensjon/kjerne/kodetabeller/SimuleringTypeCode.java
 */
enum class PenSimuleringType(val externalValue: String, val internalValue: SimuleringType) {

    ALDERSPENSJON("ALDER", SimuleringType.ALDERSPENSJON),
    ALDERSPENSJON_MED_AFP_PRIVAT("ALDER_M_AFP_PRIVAT", SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT),
    ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG("ALDER_MED_AFP_OFFENTLIG_LIVSVARIG", SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG),
    ENDRING_ALDERSPENSJON("ENDR_ALDER", SimuleringType.ENDRING_ALDERSPENSJON),
    ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT("ENDR_AP_M_AFP_PRIVAT", SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT),
    ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG("ENDR_AP_M_AFP_OFFENTLIG_LIVSVARIG", SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG);

    companion object {
        private val values = entries.toTypedArray()

        fun fromInternalValue(value: SimuleringType) =
            values.singleOrNull { it.internalValue == value } ?: ALDERSPENSJON
    }
}
