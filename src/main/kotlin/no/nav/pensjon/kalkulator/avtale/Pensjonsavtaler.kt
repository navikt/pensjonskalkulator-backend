package no.nav.pensjon.kalkulator.avtale

data class Pensjonsavtaler(
    val avtaler: List<Pensjonsavtale>,
    val utilgjengeligeSelskap: List<Selskap>
)
