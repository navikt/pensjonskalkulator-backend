package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp

/**
 * https://github.com/navikt/tp/blob/main/tp-api/src/main/kotlin/no/nav/samhandling/tp/domain/codestable/YtelseTypeCode.kt
 */
enum class TpYtelseType(val externalValue: String) {
    ALDERSPENSJON("ALDER"),
    UFOEREPENSJON("UFORE"),
    GJENLEVENDEPENSJON("GJENLEVENDE"),
    BARNEPENSJON("BARN"),
    AFP("AFP"), // avtalefestet pensjon
    UKJENT("UKJENT")
}
