package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.simulering.AfpOrdningType

enum class SimulatorAfpOrdningType (val externalValue: String, val internalValue: AfpOrdningType) {

    AFPKOM("AFPKOM", AfpOrdningType.AFPKOM),
    AFPSTAT("AFPSTAT", AfpOrdningType.AFPSTAT),
    FINANS("FINANS", AfpOrdningType.FINANS),
    KONV_K("KONV_K", AfpOrdningType.KONV_K),
    KONV_O("KONV_O", AfpOrdningType.KONV_O),
    LONHO("LONHO", AfpOrdningType.LONHO),
    NAVO("NAVO", AfpOrdningType.NAVO);

    companion object {
        private val values = SimulatorAfpOrdningType.entries.toTypedArray()

        fun fromInternalValue(value: AfpOrdningType?) =
            values.singleOrNull { it.internalValue == value } ?: AFPKOM
    }
}