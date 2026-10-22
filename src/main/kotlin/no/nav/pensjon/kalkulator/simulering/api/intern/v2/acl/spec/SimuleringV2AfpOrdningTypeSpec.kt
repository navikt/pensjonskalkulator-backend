package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import no.nav.pensjon.kalkulator.simulering.AfpOrdningType

enum class SimuleringV2AfpOrdningTypeSpec(val internalValue: AfpOrdningType) {
    KOMMUNAL(internalValue = AfpOrdningType.AFPKOM),
    STATLIG(internalValue = AfpOrdningType.AFPSTAT),
    FINANSNAERINGEN(internalValue = AfpOrdningType.FINANS),
    KONVERTERT_PRIVAT(internalValue = AfpOrdningType.KONV_K),
    KONVERTERT_OFFENTLIG(internalValue = AfpOrdningType.KONV_O),
    LO_NHO_ORDNINGEN(internalValue = AfpOrdningType.LONHO),
    SPEKTER(internalValue = AfpOrdningType.NAVO)
}