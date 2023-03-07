package no.nav.pensjon.kalkulator.opptjening

enum class Opptjeningstype(val code: String) {

    PENSJONSGIVENDE_INNTEKT("PPI"),
    SUM_PENSJONSGIVENDE_INNTEKT("SUM_PI"),
    OTHER("OTHER");

    companion object {
        fun forCode(code: String): Opptjeningstype =
            Opptjeningstype.values().firstOrNull { it.code == code } ?: OTHER
    }
}
