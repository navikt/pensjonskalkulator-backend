package no.nav.pensjon.kalkulator.avtale.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.avtale.AvtaleKategori
import no.nav.pensjon.kalkulator.general.Alder

data class PensjonsavtaleResultV3(
    @field:NotNull val avtaler: List<PensjonsavtaleV3>,
    @field:NotNull val utilgjengeligeSelskap: List<SelskapV3>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PensjonsavtaleV3(
    @field:NotNull val produktbetegnelse: String,
    @field:NotNull val kategori: AvtaleKategoriV3,
    @field:NotNull val startAar: Int, // år som i alder – NB: avtaler uten startår returneres ikke
    val sluttAar: Int?, // år som i alder
    @field:NotNull val utbetalingsperioder: List<UtbetalingsperiodeV3>
)

data class SelskapV3(
    @field:NotNull val navn: String,
    @field:NotNull val heltUtilgjengelig: Boolean
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UtbetalingsperiodeV3(
    @field:NotNull val startAlder: Alder,
    val sluttAlder: Alder?,
    @field:NotNull val aarligUtbetaling: Int,
    @field:NotNull val grad: Int
)

enum class AvtaleKategoriV3(val internalValue: AvtaleKategori) {
    UNKNOWN(AvtaleKategori.UNKNOWN),
    INDIVIDUELL_ORDNING(AvtaleKategori.INDIVIDUELL_ORDNING),
    PRIVAT_TJENESTEPENSJON(AvtaleKategori.PRIVAT_TJENESTEPENSJON);

    companion object {
        private val values = entries.toTypedArray()

        fun fromInternalValue(value: AvtaleKategori?) =
            values.singleOrNull { it.internalValue == value } ?: UNKNOWN
    }
}
