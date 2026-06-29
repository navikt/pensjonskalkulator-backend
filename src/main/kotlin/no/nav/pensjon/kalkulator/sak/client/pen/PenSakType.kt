package no.nav.pensjon.kalkulator.sak.client.pen

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.sak.SakType
import org.springframework.util.StringUtils.hasLength

/**
 * The 'externalValue' corresponds with
 * no.nav.domain.pensjon.kjerne.kodetabeller.SakTypeCode
 * in pensjon-pen
 */
enum class PenSakType(val externalValue: String, val internalValue: SakType) {
    AVTALEFESTET_PENSJON_I_OFFENTLIG_SEKTOR(
        externalValue = "AFP",
        internalValue = SakType.AVTALEFESTET_PENSJON_I_OFFENTLIG_SEKTOR
    ),
    AVTALEFESTET_PENSJON_I_PRIVAT_SEKTOR(
        externalValue = "AFP_PRIVAT",
        internalValue = SakType.AVTALEFESTET_PENSJON_I_PRIVAT_SEKTOR
    ),
    ALDERSPENSJON(
        externalValue = "ALDER",
        internalValue = SakType.ALDERSPENSJON
    ),
    BARNEPENSJON(
        externalValue = "BARNEP",
        internalValue = SakType.BARNEPENSJON
    ),
    FAMILIEPLEIERYTELSE(
        externalValue = "FAM_PL",
        internalValue = SakType.FAMILIEPLEIERYTELSE
    ),
    GAMMEL_YRKESSKADE(
        externalValue = "GAM_YRK",
        internalValue = SakType.GAMMEL_YRKESSKADE
    ),
    GENERELL(
        externalValue = "GENRL",
        internalValue = SakType.GENERELL
    ),
    GJENLEVENDEYTELSE(
        externalValue = "GJENLEV",
        internalValue = SakType.GJENLEVENDEYTELSE
    ),
    GRUNNBLANKETTER(
        externalValue = "GRBL",
        internalValue = SakType.GRUNNBLANKETTER
    ),
    KRIGSPENSJON(
        externalValue = "KRIGSP",
        internalValue = SakType.KRIGSPENSJON
    ),
    OMSORGSOPPTJENING(
        externalValue = "OMSORG",
        internalValue = SakType.OMSORGSOPPTJENING
    ),
    UFOERETRYGD(
        externalValue = "UFOREP", // UFOREP = Uførepensjon (tidligere begrep)
        internalValue = SakType.UFOERETRYGD
    ),
    // Special values not in pensjon-pen (used for missing/unknown values):
    NONE(
        externalValue = "",
        internalValue = SakType.NONE
    ),
    UNKNOWN(
        externalValue = "?",
        internalValue = SakType.UNKNOWN
    );

    companion object {
        private val log = KotlinLogging.logger {}

        fun internalValue(externalValue: String?): SakType =
            fromExternalValue(externalValue).internalValue

        fun fromInternalValue(value: SakType?) =
            entries.singleOrNull { it.internalValue == value }

        private fun fromExternalValue(value: String?) =
            entries.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown PEN sak-type '$externalValue'" } }
            else
                NONE
    }
}