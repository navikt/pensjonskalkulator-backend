package no.nav.pensjon.kalkulator.sak

enum class SakType(val relevant: Boolean) {
    NONE(false),
    UNKNOWN(false),
    GJENLEVENDEYTELSE(true),
    UFOEREPENSJON(true),
}
