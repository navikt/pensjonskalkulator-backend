package no.nav.pensjon.kalkulator.avtale

enum class AvtaleKategori(val included: Boolean) {
    NONE(true),
    UNKNOWN(true),
    INDIVIDUELL_ORDNING(true),
    PRIVAT_AFP(false),
    PRIVAT_TJENESTEPENSJON(true),
    OFFENTLIG_TJENESTEPENSJON(false),
    FOLKETRYGD(false);
}
