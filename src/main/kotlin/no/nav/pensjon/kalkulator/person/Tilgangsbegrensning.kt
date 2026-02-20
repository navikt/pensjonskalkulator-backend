package no.nav.pensjon.kalkulator.person

// TODO Merge with AdressebeskyttelseGradering?
enum class Tilgangsbegrensning(val erStreng: Boolean = true) {
    FORTROLIG(erStreng = false),
    STRENGT_FORTROLIG,
    STRENGT_FORTROLIG_UTLAND,
    UNKNOWN
}