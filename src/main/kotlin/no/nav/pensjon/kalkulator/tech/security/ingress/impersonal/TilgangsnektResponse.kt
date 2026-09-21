package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal

/**
 * Responsbody ved tilgangsnekt.
 *
 * 'type' er en stabil, maskinlesbar diskriminator som frontend kan rute på,
 * mens 'detail' er menneskelesbar tekst som ikke må brukes til logikk.
 */
data class TilgangsnektResponse(
    val type: String = TILGANGSNEKT_TYPE,
    val detail: String?
) {
    companion object {
        const val TILGANGSNEKT_TYPE = "TILGANGSNEKT"
    }
}
