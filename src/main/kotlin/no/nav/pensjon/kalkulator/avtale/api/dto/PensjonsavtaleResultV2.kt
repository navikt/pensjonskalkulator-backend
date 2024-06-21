package no.nav.pensjon.kalkulator.avtale.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import no.nav.pensjon.kalkulator.avtale.AvtaleKategori
import no.nav.pensjon.kalkulator.general.Alder

data class PensjonsavtaleResultV2(
    val avtaler: List<PensjonsavtaleV2>,
    val utilgjengeligeSelskap: List<SelskapV2>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PensjonsavtaleV2(
    val produktbetegnelse: String,
    val kategori: AvtaleKategoriV2,
    val startAar: Int, // år som i alder – NB: avtaler uten startår returneres ikke
    val sluttAar: Int?, // år som i alder
    val utbetalingsperioder: List<UtbetalingsperiodeV2>
)

data class SelskapV2(
    val navn: String,
    val heltUtilgjengelig: Boolean
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UtbetalingsperiodeV2(
    val startAlder: Alder,
    val sluttAlder: Alder?,
    val aarligUtbetaling: Int,
    val grad: Int
)

enum class AvtaleKategoriV2(val internalValue: AvtaleKategori) {
    UNKNOWN(AvtaleKategori.UNKNOWN),
    INDIVIDUELL_ORDNING(AvtaleKategori.INDIVIDUELL_ORDNING),
    PRIVAT_TJENESTEPENSJON(AvtaleKategori.PRIVAT_TJENESTEPENSJON);

    companion object {
        private val values = entries.toTypedArray()

        fun fromInternalValue(value: AvtaleKategori?) =
            values.singleOrNull { it.internalValue == value } ?: UNKNOWN
    }
}
