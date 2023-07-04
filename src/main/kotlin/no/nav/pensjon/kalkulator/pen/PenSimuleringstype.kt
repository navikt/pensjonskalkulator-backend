package no.nav.pensjon.kalkulator.pen

import no.nav.pensjon.kalkulator.simulering.SimuleringType

enum class PenSimuleringstype(val type: SimuleringType) {
    ALDER(SimuleringType.ALDERSPENSJON),
    ALDER_M_AFP_PRIVAT(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT);

    companion object {
        fun from(type: SimuleringType): PenSimuleringstype =
            when (type) {
                SimuleringType.ALDERSPENSJON -> ALDER
                SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT -> ALDER_M_AFP_PRIVAT
            }
    }
}