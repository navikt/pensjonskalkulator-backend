package no.nav.pensjon.kalkulator.land

data class LandInfo(
    val landkode: String,
    val erAvtaleland: Boolean,
    val bokmaalNavn: String,
    val nynorskNavn: String,
    val engelskNavn: String
)
