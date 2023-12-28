package no.nav.pensjon.kalkulator.avtale

/**
 * Årsaker til at beregning ikke er utført av ekstern tjeneste.
 */
enum class ManglendeEksternBeregningAarsak {
    NONE,
    UNKNOWN,
    UKJENT_PRODUKTTYPE,
    UTILSTREKKELIG_DATA
}
