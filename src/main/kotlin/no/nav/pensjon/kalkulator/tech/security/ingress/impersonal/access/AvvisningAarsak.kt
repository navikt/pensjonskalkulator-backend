package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access

enum class AvvisningAarsak {
    STRENGT_FORTROLIG_ADRESSE,
    STRENGT_FORTROLIG_UTLAND,
    AVDOED,
    VERGEMAAL,
    PERSON_UTLAND,
    SKJERMING,
    FORTROLIG_ADRESSE,
    UKJENT_BOSTED,
    GEOGRAFISK,
    HABILITET,
    MANGLENDE_FAGGRUPPE_MEDLEMSKAP,
    POPULASJONSTILGANGSSJEKK_FEIL,
    TILGANGSSJEKK_FEIL,

    // Special value for handling unexpected/missing enum values:
    UNKNOWN
}