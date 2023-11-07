package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.AvtaleUnderkategori
import org.springframework.util.StringUtils.hasLength

enum class Underkategori(val externalValue: String, val internalValue: AvtaleUnderkategori) {
    NONE("", AvtaleUnderkategori.NONE),
    UNKNOWN("?", AvtaleUnderkategori.UNKNOWN),
    FORENINGSKOLLEKTIV("foreningskollektiv", AvtaleUnderkategori.FORENINGSKOLLEKTIV),
    FORETAKSPENSJON("foretakspensjon", AvtaleUnderkategori.FORETAKSPENSJON),
    FORTSETTELSESFORSIKRING("fortsettelsesforsikring", AvtaleUnderkategori.FORTSETTELSESFORSIKRING),
    FRIPOLISE("fripolise", AvtaleUnderkategori.FRIPOLISE),
    FRIPOLISE_MED_INVESTERINGSVALG("fripolise med investeringsvalg", AvtaleUnderkategori.FRIPOLISE_MED_INVESTERINGSVALG),
    HYBRIDPENSJON_MED_GARANTERT_REGULERING("hybridpensjon med garantert regulering", AvtaleUnderkategori.HYBRIDPENSJON_MED_GARANTERT_REGULERING),
    HYBRIDPENSJON_MED_INDIVIDUELT_INVESTERINGSVALG("hybridpensjon med individuelt investeringsvalg", AvtaleUnderkategori.HYBRIDPENSJON_MED_INDIVIDUELT_INVESTERINGSVALG),
    HYBRIDPENSJON_MED_NULLGARANTI("hybridpensjon med nullgaranti", AvtaleUnderkategori.HYBRIDPENSJON_MED_NULLGARANTI),
    INDIVIDUELL_LIVRENTE("individuell livrente", AvtaleUnderkategori.INDIVIDUELL_LIVRENTE),
    INNSKUDDSPENSJON("innskuddspensjon", AvtaleUnderkategori.INNSKUDDSPENSJON),
    IPA("ipa", AvtaleUnderkategori.IPA),
    IPS_2008("ips 2008", AvtaleUnderkategori.IPS_2008),
    IPS_2017("ips 2017", AvtaleUnderkategori.IPS_2017),
    KAPITALFORSIKRING("kapitalforsikring", AvtaleUnderkategori.KAPITALFORSIKRING),
    KOLLEKTIV_LIVRENTE("kollektiv livrente", AvtaleUnderkategori.KOLLEKTIV_LIVRENTE),
    OPPSATT_PENSJON("oppsatt pensjon", AvtaleUnderkategori.OPPSATT_PENSJON),
    PENSJONSBEVIS("pensjonsbevis", AvtaleUnderkategori.PENSJONSBEVIS),
    PENSJONSBEVIS_MED_INDIVIDUELT_INVESTERINGSVALG("pensjonsbevis med individuelt investeringsvalg", AvtaleUnderkategori.PENSJONSBEVIS_MED_INDIVIDUELT_INVESTERINGSVALG),
    PENSJONSBEVIS_MED_NULLGARANTI("pensjonsbevis med nullgaranti", AvtaleUnderkategori.PENSJONSBEVIS_MED_NULLGARANTI),
    PENSJONSKAPITALBEVIS("pensjonskapitalbevis", AvtaleUnderkategori.PENSJONSKAPITALBEVIS),
    PENSJONSKAPITALBEVIS_IPS_2008("pensjonskapitalbevis ips 2008", AvtaleUnderkategori.PENSJONSKAPITALBEVIS_IPS_2008),
    PENSJONSKAPITALBEVIS_IPS_2017("pensjonskapitalbevis ips 2017", AvtaleUnderkategori.PENSJONSKAPITALBEVIS_IPS_2017);

    companion object {
        private val values = entries.toTypedArray()
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown NP underkategori '$externalValue'" } }
            else
                NONE
    }
}
