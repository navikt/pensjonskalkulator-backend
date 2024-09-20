package no.nav.pensjon.kalkulator.vedtak.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoependeVedtakV1(
    val alderspensjon: LoependeSakV1?,
    val ufoeretrygd: LoependeSakV1?,
    val afpPrivat: LoependeSakV1?,
    val afpOffentlig: LoependeSakV1?
)

data class LoependeSakV1(
    val loepende: Boolean = false,
    val grad: Int = 100
)
