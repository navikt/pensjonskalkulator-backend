package no.nav.pensjon.kalkulator.person

/**
 * Sivilstatus er et mer uformelt begrep enn sivilstand, og inkluderer også 'samboer'.
 */
enum class Sivilstatus(val harEps: Boolean = false) {
    UNKNOWN,
    UOPPGITT,
    UGIFT,
    GIFT(harEps = true),
    ENKE_ELLER_ENKEMANN,
    SKILT,
    SEPARERT,
    REGISTRERT_PARTNER(harEps = true),
    SEPARERT_PARTNER,
    SKILT_PARTNER,
    GJENLEVENDE_PARTNER,
    SAMBOER(harEps = true)
}