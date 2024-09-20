package no.nav.pensjon.kalkulator.vedtak.api

data class LoependeVedtakDto(
    val alderspensjon: LoependeSakDto?,
    val ufoeretrygd: LoependeSakDto?,
    val afpPrivat: LoependeSakDto?,
    val afpOffentlig: LoependeSakDto?,
)

data class LoependeSakDto(
    val loepende: Boolean = false,
    val grad: Int = 100
)
