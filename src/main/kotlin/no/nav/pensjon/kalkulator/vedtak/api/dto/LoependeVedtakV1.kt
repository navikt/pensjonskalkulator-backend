package no.nav.pensjon.kalkulator.vedtak.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoependeVedtakV1(
    val alderspensjon: LoependeVedtakDetaljerV1,
    val ufoeretrygd: LoependeVedtakDetaljerV1,
    val afpPrivat: LoependeVedtakDetaljerV1,
    val afpOffentlig: LoependeVedtakDetaljerV1,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoependeVedtakDetaljerV1(
    val loepende: Boolean = false,
    val grad: Int = 0,
    val fom: LocalDate? = null,
)
