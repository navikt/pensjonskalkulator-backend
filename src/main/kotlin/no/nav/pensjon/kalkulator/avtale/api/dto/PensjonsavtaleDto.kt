package no.nav.pensjon.kalkulator.avtale.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import no.nav.pensjon.kalkulator.avtale.AvtaleKategori
import no.nav.pensjon.kalkulator.general.Alder

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PensjonsavtaleDto(
    val produktbetegnelse: String,
    val kategori: AvtaleKategori,
    val startAar: Int?, // år som i alder – NB: ikke påkrevd
    val sluttAar: Int?, // år som i alder
    val utbetalingsperioder: List<UtbetalingsperiodeDto>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UtbetalingsperiodeDto(
    val startAlder: Alder, // maaneder = 0..11
    val sluttAlder: Alder?, // maaneder = 0..11
    val aarligUtbetaling: Int,
    val grad: Int
)
