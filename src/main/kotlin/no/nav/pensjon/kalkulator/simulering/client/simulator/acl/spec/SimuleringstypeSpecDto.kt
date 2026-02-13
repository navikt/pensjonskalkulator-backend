package no.nav.pensjon.kalkulator.simulering.client.simulator.acl.spec

import no.nav.pensjon.kalkulator.simulering.SimuleringType

enum class SimuleringstypeSpecDto(val internalValue: SimuleringType) {
    ALDERSPENSJON_MED_TIDSBEGRENSET_OFFENTLIG_AFP(internalValue = SimuleringType.PRE2025_OFFENTLIG_AFP_ETTERFULGT_AV_ALDERSPENSJON),
    ALDERSPENSJON(internalValue = SimuleringType.ALDERSPENSJON),
    ALDERSPENSJON_MED_PRIVAT_AFP(internalValue = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT),
    ALDERSPENSJON_MED_LIVSVARIG_OFFENTLIG_AFP(internalValue = SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG),
    ALDERSPENSJON_MED_GJENLEVENDERETT(internalValue = SimuleringType.ALDERSPENSJON_MED_GJENLEVENDERETT),
    ENDRING_ALDERSPENSJON(internalValue = SimuleringType.ENDRING_ALDERSPENSJON),
    ENDRING_ALDERSPENSJON_MED_PRIVAT_AFP(internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT),
    ENDRING_ALDERSPENSJON_MED_LIVSVARIG_OFFENTLIG_AFP(internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG),
    ENDRING_ALDERSPENSJON_MED_GJENLEVENDERETT(internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_GJENLEVENDERETT);

    companion object {
        private val values = entries.toTypedArray()

        fun fromInternalValue(value: SimuleringType): SimuleringstypeSpecDto =
            values.singleOrNull { it.internalValue == value } ?: ALDERSPENSJON
    }
}
