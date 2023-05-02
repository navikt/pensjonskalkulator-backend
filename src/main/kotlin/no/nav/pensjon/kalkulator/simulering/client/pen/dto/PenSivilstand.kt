package no.nav.pensjon.kalkulator.simulering.client.pen.dto

enum class PenSivilstand {
    ENKE, // Enke/enkemann
    GIFT, // Gift
    GJES, // Gjenlevende etter samlivsbrudd
    GJPA, // Gjenlevende partner
    GJSA, // Gjenlevende samboer
    GLAD, // Gift, lever adskilt
    NULL, // Udefinert
    PLAD, // Registrert partner, lever adskilt
    REPA, // Registrert partner
    SAMB, // Samboer
    SEPA, // Separert partner
    SEPR, // Separert
    SKIL, // Skilt
    SKPA, // Skilt partner
    UGIF // Ugift
}
