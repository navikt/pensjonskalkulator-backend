package no.nav.pensjon.kalkulator.sak

enum class SakStatus(val relevant: Boolean) {
    NONE(false),
    UNKNOWN(false),
    OPPRETTET(false),
    TIL_BEHANDLING(false),
    LOEPENDE(true),
    AVSLUTTET(false)
}
