package no.nav.pensjon.kalkulator.avtale

/**
 * Årsaker til at beregning ikke er utført av ekstern tjeneste.
 */
enum class ManglendeEksternBeregningAarsak {
    GENERELL_FEIL_MANGLENDE_PROGNOSE,
    UKJENT_PRODUKTTYPE,
    UTILSTREKKELIG_DATA,
    // Special cases:
    NONE,
    UNKNOWN
}