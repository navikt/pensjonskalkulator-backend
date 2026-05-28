package no.nav.pensjon.kalkulator.ansatt.enhet.client.navansatt.acl

/**
 * Data transfer object (DTO) for a tjenestekontor-enhet.
 * The fields are specified by NAVEnhetResult in navansatt, ref.
 * github.com/navikt/navansatt/blob/main/src/main/kotlin/no/nav/navansatt/Routes.kt
 */
data class NavEnhetResultDto(
    val id: String,
    val navn: String,
    val nivaa: String
)

data class NavEnhetProblemDto(
    val message: String
)