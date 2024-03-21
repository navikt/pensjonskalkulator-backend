package no.nav.pensjon.kalkulator.simulering

data class Vilkaarsproeving(
    val innvilget: Boolean,
    val alternativ: Alternativ? = null
)
