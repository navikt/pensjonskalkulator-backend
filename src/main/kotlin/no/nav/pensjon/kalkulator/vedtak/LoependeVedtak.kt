package no.nav.pensjon.kalkulator.vedtak

data class LoependeVedtak(
    val alderspensjon: Grad?,
    val ufoeretrygd: Grad?,
    val afpPrivat: Grad?,
    val afpOffentlig: Grad?,
)

data class Grad(
    val grad: Int,
)
