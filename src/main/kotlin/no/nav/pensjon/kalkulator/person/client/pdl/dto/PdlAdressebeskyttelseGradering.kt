package no.nav.pensjon.kalkulator.person.client.pdl.dto

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import org.springframework.util.StringUtils.hasLength

/**
 * For externalValue definitions see: https://pdldocs-navno.msappproxy.net/ekstern/index.html#_adressebeskyttelse
 */
enum class PdlAdressebeskyttelseGradering(val externalValue: String, val internalValue: AdressebeskyttelseGradering) {

    UNKNOWN("?", AdressebeskyttelseGradering.UNKNOWN),
    FORTROLIG("FORTROLIG", AdressebeskyttelseGradering.FORTROLIG),
    STRENGT_FORTROLIG("STRENGT_FORTROLIG", AdressebeskyttelseGradering.STRENGT_FORTROLIG),
    STRENGT_FORTROLIG_UTLAND("STRENGT_FORTROLIG_UTLAND", AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND),
    UGRADERT("UGRADERT", AdressebeskyttelseGradering.UGRADERT);

    companion object {
        private val values = entries.toTypedArray()
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown PDL gradering '$externalValue'" } }
            else
                UGRADERT
    }
}
