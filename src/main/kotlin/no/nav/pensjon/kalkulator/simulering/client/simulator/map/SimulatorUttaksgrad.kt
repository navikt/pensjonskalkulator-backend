package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.general.Uttaksgrad

/**
 * The 'externalValue' is uttaksgrad values used by pensjonssimulator.
 */
enum class SimulatorUttaksgrad(val externalValue: String, val internalValue: Uttaksgrad) {

    NULL("P_0", Uttaksgrad.NULL),
    TJUE_PROSENT("P_20", Uttaksgrad.TJUE_PROSENT),
    FOERTI_PROSENT("P_40", Uttaksgrad.FOERTI_PROSENT),
    FEMTI_PROSENT("P_50", Uttaksgrad.FEMTI_PROSENT),
    SEKSTI_PROSENT("P_60", Uttaksgrad.SEKSTI_PROSENT),
    AATTI_PROSENT("P_80", Uttaksgrad.AATTI_PROSENT),
    HUNDRE_PROSENT("P_100", Uttaksgrad.HUNDRE_PROSENT);

    companion object {
        fun fromInternalValue(grad: Uttaksgrad?): SimulatorUttaksgrad =
            entries.firstOrNull { it.internalValue == grad } ?: HUNDRE_PROSENT
    }
}
