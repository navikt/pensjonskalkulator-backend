package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import no.nav.pensjon.kalkulator.simulering.SimuleringType

enum class SimuleringV2SimuleringstypeSpec(val internalValue: SimuleringType) {
    ALDERSPENSJON(internalValue = SimuleringType.ALDERSPENSJON),
    ALDERSPENSJON_MED_TIDSBEGRENSET_OFFENTLIG_AFP(internalValue = SimuleringType.PRE2025_OFFENTLIG_AFP_ETTERFULGT_AV_ALDERSPENSJON),
    ALDERSPENSJON_MED_LIVSVARIG_OFFENTLIG_AFP(internalValue = SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG),
    ALDERSPENSJON_MED_PRIVAT_AFP(internalValue = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT),
    ALDERSPENSJON_MED_GJENLEVENDERETT(internalValue = SimuleringType.ALDERSPENSJON_MED_GJENLEVENDERETT),
    ENDRING_ALDERSPENSJON(internalValue = SimuleringType.ENDRING_ALDERSPENSJON),
    ENDRING_ALDERSPENSJON_MED_LIVSVARIG_OFFENTLIG_AFP(internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG),
    ENDRING_ALDERSPENSJON_MED_PRIVAT_AFP(internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT),
    ENDRING_ALDERSPENSJON_MED_GJENLEVENDERETT(internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_GJENLEVENDERETT),
    SERVICEBEREGN_AFP(internalValue = SimuleringType.SERVICEBEREGN_AFP)
    // ALDERSPENSJON_MED_TIDSBEGRENSET_OFFENTLIG_AFP har ingen tilsvarende type for endring (støttes ikke)
}