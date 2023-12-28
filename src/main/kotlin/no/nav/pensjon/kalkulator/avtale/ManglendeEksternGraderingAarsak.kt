package no.nav.pensjon.kalkulator.avtale

/**
 * Årsaker til at gradering ikke er utført av ekstern tjeneste.
 */
enum class ManglendeEksternGraderingAarsak {
    NONE,
    UNKNOWN,
    IKKE_STOETTET,
    IKKE_TILLATT
}
