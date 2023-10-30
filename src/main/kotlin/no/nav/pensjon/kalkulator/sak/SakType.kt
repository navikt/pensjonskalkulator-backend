package no.nav.pensjon.kalkulator.sak

enum class SakType(val relevant: Boolean) {
    NONE(false),
    UNKNOWN(false),
    GENERELL(false),
    GJENLEVENDEYTELSE(true),
    OMSORGSOPPTJENING(false),
    UFOERETRYGD(true),
}
