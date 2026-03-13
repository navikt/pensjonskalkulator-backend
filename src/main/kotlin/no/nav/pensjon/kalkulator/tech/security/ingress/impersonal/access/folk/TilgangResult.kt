package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk

data class TilgangResult(
    val innvilget: Boolean,
    val avvisningAarsak: AvvisningAarsak?,
    val begrunnelse: String?,
    val traceId: String?
) {
    val avvisningsinfo: String = "$avvisningAarsak: $begrunnelse"
}

enum class AvvisningAarsak {
    STRENGT_FORTROLIG_ADRESSE,
    STRENGT_FORTROLIG_UTLAND,
    AVDOED,
    PERSON_UTLAND,
    SKJERMING,
    FORTROLIG_ADRESSE,
    UKJENT_BOSTED,
    GEOGRAFISK,
    HABILITET,
    POPULASJONSTILGANGSSJEKK_FEILET
}
