package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import no.nav.pensjon.kalkulator.avtale.AvtaleKategori
import org.springframework.util.StringUtils.hasLength

enum class Kategori(val externalValue: String, val internalValue: AvtaleKategori) {
    NONE("", AvtaleKategori.NONE),
    UNKNOWN("?", AvtaleKategori.UNKNOWN),
    INDIVIDUELL_ORDNING("individuellOrdning", AvtaleKategori.INDIVIDUELL_ORDNING),
    PRIVAT_AFP("privatAFP", AvtaleKategori.PRIVAT_AFP),
    PRIVAT_TJENESTEPENSJON("privatTjenestepensjon", AvtaleKategori.PRIVAT_TJENESTEPENSJON),
    OFFENTLIG_TJENESTEPENSJON("offentligTjenestepensjon", AvtaleKategori.OFFENTLIG_TJENESTEPENSJON),
    FOLKETRYGD("folketrygd", AvtaleKategori.FOLKETRYGD);

    companion object {
        private val values = values()

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) = if (hasLength(externalValue)) UNKNOWN else NONE
    }
}
