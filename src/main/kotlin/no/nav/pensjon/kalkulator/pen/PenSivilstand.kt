package no.nav.pensjon.kalkulator.pen

import no.nav.pensjon.kalkulator.person.Sivilstand

/**
 * Sivilstand values used by PEN (pensjonsfaglig kjerne).
 * PEN supports the concept of 'samboer' (unlike Folkeregisteret/PDL).
 * The source of PEN's sivilstand values is:
 * https://github.com/navikt/pesys/blob/main/pen/domain/nav-domain-pensjon-pen-java/src/main/java/no/nav/domain/pensjon/kjerne/kodetabeller/SivilstandTypeCode.java
 */
enum class PenSivilstand(val sivilstand: Sivilstand) {

    ENKE(Sivilstand.ENKE_ELLER_ENKEMANN), // Enke/enkemann
    GIFT(Sivilstand.GIFT), // Gift
    GJES(Sivilstand.UOPPGITT), // Gjenlevende etter samlivsbrudd
    GJPA(Sivilstand.GJENLEVENDE_PARTNER), // Gjenlevende partner
    GJSA(Sivilstand.UOPPGITT), // Gjenlevende samboer
    GLAD(Sivilstand.GIFT), // Gift, lever adskilt
    NULL(Sivilstand.UOPPGITT), // Udefinert
    PLAD(Sivilstand.REGISTRERT_PARTNER), // Registrert partner, lever adskilt
    REPA(Sivilstand.REGISTRERT_PARTNER), // Registrert partner
    SAMB(Sivilstand.UOPPGITT), // Samboer
    SEPA(Sivilstand.SEPARERT_PARTNER), // Separert partner
    SEPR(Sivilstand.SEPARERT), // Separert
    SKIL(Sivilstand.SKILT), // Skilt
    SKPA(Sivilstand.SKILT_PARTNER), // Skilt partner
    UGIF(Sivilstand.UGIFT); // Ugift

    companion object {
        fun from(sivilstand: Sivilstand): PenSivilstand =
            when (sivilstand) {
                Sivilstand.UOPPGITT -> NULL // ambiguous
                Sivilstand.GIFT -> GIFT // ambiguous
                Sivilstand.REGISTRERT_PARTNER -> REPA // ambiguous
                else -> PenSivilstand.values().firstOrNull { it.sivilstand == sivilstand } ?: NULL
            }
    }
}
