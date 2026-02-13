package no.nav.pensjon.kalkulator.simulering.client.simulator.acl.spec

import no.nav.pensjon.kalkulator.simulering.AfpOrdningType

enum class AfpOrdningTypeSpecDto(val internalValue: AfpOrdningType) {
    KOMMUNAL(internalValue = AfpOrdningType.AFPKOM),
    STATLIG(internalValue = AfpOrdningType.AFPSTAT),
    FINANSNAERINGEN(internalValue = AfpOrdningType.FINANS),
    KONVERTERT_PRIVAT(internalValue = AfpOrdningType.KONV_K),
    KONVERTERT_OFFENTLIG(internalValue = AfpOrdningType.KONV_O),
    LO_NHO_ORDNINGEN(internalValue = AfpOrdningType.LONHO),
    SPEKTER(internalValue = AfpOrdningType.NAVO);

    companion object {
        private val values = entries.toTypedArray()

        fun fromInternalValue(value: AfpOrdningType): AfpOrdningTypeSpecDto =
            values.singleOrNull { it.internalValue == value } ?: STATLIG
    }
}