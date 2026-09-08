package no.nav.pensjon.kalkulator.avtale.client.np.rest.acl

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.AvtaleKategori
import org.springframework.util.StringUtils.hasLength

enum class Kategori(val externalValue: String, val internalValue: AvtaleKategori) {
    INDIVIDUELL_ORDNING(
        externalValue = "INDIVIDUELLE_ORDNINGER",
        internalValue = AvtaleKategori.INDIVIDUELL_ORDNING
    ),
    PRIVAT_AFP(
        externalValue = "PRIVAT_AFP",
        internalValue = AvtaleKategori.PRIVAT_AFP
    ),
    PRIVAT_TJENESTEPENSJON(
        externalValue = "PRIVAT_TJENESTEPENSJON",
        internalValue = AvtaleKategori.PRIVAT_TJENESTEPENSJON
    ),
    OFFENTLIG_TJENESTEPENSJON(
        externalValue = "OFFENTLIG_TJENESTEPENSJON",
        internalValue = AvtaleKategori.OFFENTLIG_TJENESTEPENSJON
    ),
    FOLKETRYGD(
        externalValue = "FOLKETRYGD",
        internalValue = AvtaleKategori.FOLKETRYGD
    ),

    // Special values not used by Norsk Pensjon (for handling unexpected/missing enum values):
    NONE(externalValue = "", internalValue = AvtaleKategori.NONE),
    UNKNOWN(externalValue = "?", internalValue = AvtaleKategori.UNKNOWN);

    companion object {
        private val log = KotlinLogging.logger {}
        private val valuesByExternal = entries.associateBy { it.externalValue }

        fun fromExternalValue(value: String?): Kategori =
            value?.let { valuesByExternal[it.uppercase()] } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown NP kategori '$externalValue'" } }
            else
                NONE
    }
}
