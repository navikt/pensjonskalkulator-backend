package no.nav.pensjon.kalkulator.sak

/**
 * "Relevant" means here that it causes redirection to detailed calculator in 'Din pensjon'
 */
enum class SakType(val relevant: Boolean = false) {
    ALDERSPENSJON,
    AVTALEFESTET_PENSJON_I_OFFENTLIG_SEKTOR,
    AVTALEFESTET_PENSJON_I_PRIVAT_SEKTOR,
    BARNEPENSJON,
    FAMILIEPLEIERYTELSE,
    GAMMEL_YRKESSKADE,
    GENERELL,
    GJENLEVENDEYTELSE(relevant = true),
    GRUNNBLANKETTER,
    KRIGSPENSJON,
    OMSORGSOPPTJENING,
    UFOERETRYGD(relevant = true),
    // Special cases:
    NONE,
    UNKNOWN
}
