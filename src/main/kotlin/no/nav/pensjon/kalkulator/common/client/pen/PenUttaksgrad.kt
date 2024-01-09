package no.nav.pensjon.kalkulator.common.client.pen

import no.nav.pensjon.kalkulator.general.Uttaksgrad

/**
 * The 'externalValue' is uttaksgrad values used by PEN (pensjonsfaglig kjerne).
 * The source of PEN's uttaksgrad values is:
 * https://github.com/navikt/pesys/blob/main/pen/domain/nav-domain-pensjon-pen-api/src/main/java/no/nav/domain/pensjon/kjerne/kodetabeller/UttaksgradCode.java
 */
enum class PenUttaksgrad(val externalValue: String, val internalValue: Uttaksgrad) {

    NULL("P_0", Uttaksgrad.NULL),
    TJUE_PROSENT("P_20", Uttaksgrad.TJUE_PROSENT),
    FOERTI_PROSENT("P_40", Uttaksgrad.FOERTI_PROSENT),
    FEMTI_PROSENT("P_50", Uttaksgrad.FEMTI_PROSENT),
    SEKSTI_PROSENT("P_60", Uttaksgrad.SEKSTI_PROSENT),
    AATTI_PROSENT("P_80", Uttaksgrad.AATTI_PROSENT),
    HUNDRE_PROSENT("P_100", Uttaksgrad.HUNDRE_PROSENT);

    companion object {
        fun fromInternalValue(grad: Uttaksgrad?): PenUttaksgrad =
            entries.firstOrNull { it.internalValue == grad } ?: HUNDRE_PROSENT
    }
}
