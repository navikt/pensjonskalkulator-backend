package no.nav.pensjon.kalkulator.sak

/**
 * "Relevant" means here that it causes redirection to detailed calculator in 'Din pensjon'
 */
enum class SakType(val relevant: Boolean) {
    NONE(false),
    UNKNOWN(false),
    AVTALEFESTET_PENSJON(false),
    BARNEPENSJON(false),
    GENERELL(false),
    GJENLEVENDEYTELSE(true),
    GRUNNBLANKETTER(false),
    OMSORGSOPPTJENING(false),
    UFOERETRYGD(true),
}
