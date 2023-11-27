package no.nav.pensjon.kalkulator.sak

/**
 * "Relevant" means here that it causes redirection to detailed calculator in 'Din pensjon'
 */
enum class SakType(val relevant: Boolean) {
    NONE(false),
    UNKNOWN(false),
    ALDERSPENSJON(false),
    AVTALEFESTET_PENSJON_I_OFFENTLIG_SEKTOR(false),
    AVTALEFESTET_PENSJON_I_PRIVAT_SEKTOR(false),
    BARNEPENSJON(false),
    GENERELL(false),
    GJENLEVENDEYTELSE(true),
    GRUNNBLANKETTER(false),
    OMSORGSOPPTJENING(false),
    UFOERETRYGD(true),
}
