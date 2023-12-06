package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.simulering.SimuleringType

enum class PenSimuleringstype(val externalValue: String, val internalValue: SimuleringType) {

    ALDERSPENSJON("ALDER", SimuleringType.ALDERSPENSJON),
    ALDERSPENSJON_MED_AFP_PRIVAT("ALDER_M_AFP_PRIVAT", SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT);

    companion object {
        private val values = entries.toTypedArray()

        fun fromInternalValue(value: SimuleringType) =
            values.singleOrNull { it.internalValue == value } ?: ALDERSPENSJON
    }
}
