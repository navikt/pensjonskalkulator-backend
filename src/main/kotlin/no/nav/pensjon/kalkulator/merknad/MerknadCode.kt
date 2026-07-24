package no.nav.pensjon.kalkulator.merknad

enum class MerknadCode {
    AFP,
    REFORM,
    INGEN_OPPTJENING,
    UFOEREGRAD,
    DAGPENGER,
    FOERSTEGANGSTJENESTE,
    OMSORGSOPPTJENING,
    GRADERT_UTTAK,
    HELT_UTTAK,

    // Special values representing missing/unknown value:
    NONE,
    UNKNOWN
}