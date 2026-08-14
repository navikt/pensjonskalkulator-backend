package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.client.tilgangsmaskin.acl

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.AvvisningAarsak
import org.springframework.util.StringUtils.hasLength

/**
 * The external values are defined in
 * tilgangsmaskin.intern.nav.no/swagger-ui/index.html#/TilgangController/kompletteRegler
 */
enum class AvvisningsKodeDto(val externalValue: String, val internalValue: AvvisningAarsak) {

    STRENGT_FORTROLIG_ADRESSE(
        externalValue = "AVVIST_STRENGT_FORTROLIG_ADRESSE",
        internalValue = AvvisningAarsak.STRENGT_FORTROLIG_ADRESSE
    ),
    STRENGT_FORTROLIG_UTLAND(
        externalValue = "AVVIST_STRENGT_FORTROLIG_UTLAND",
        internalValue = AvvisningAarsak.STRENGT_FORTROLIG_UTLAND
    ),
    AVDOED(
        externalValue = "AVVIST_AVDØD",
        internalValue = AvvisningAarsak.AVDOED
    ),
    VERGEMAAL(
        externalValue = "AVVIST_VERGEMÅL",
        internalValue = AvvisningAarsak.VERGEMAAL
    ),
    PERSON_UTLAND(
        externalValue = "AVVIST_PERSON_UTLAND",
        internalValue = AvvisningAarsak.PERSON_UTLAND
    ),
    SKJERMING(
        externalValue = "AVVIST_SKJERMING",
        internalValue = AvvisningAarsak.SKJERMING
    ),
    FORTROLIG_ADRESSE(
        externalValue = "AVVIST_FORTROLIG_ADRESSE",
        internalValue = AvvisningAarsak.FORTROLIG_ADRESSE
    ),
    UKJENT_BOSTED(
        externalValue = "AVVIST_UKJENT_BOSTED",
        internalValue = AvvisningAarsak.UKJENT_BOSTED
    ),
    GEOGRAFISK(
        externalValue = "AVVIST_GEOGRAFISK",
        internalValue = AvvisningAarsak.GEOGRAFISK
    ),
    HABILITET(
        externalValue = "AVVIST_HABILITET",
        internalValue = AvvisningAarsak.HABILITET
    ),

    // Special value not used by tilgangsmaskinen (for handling unexpected/missing enum values):
    UNKNOWN(externalValue = "?", internalValue = AvvisningAarsak.UNKNOWN);

    companion object {
        private val log = KotlinLogging.logger {}

        val valuesByExternal = AvvisningsKodeDto.entries.associateBy { it.externalValue }

        fun internalValue(value: String?): AvvisningAarsak =
            fromExternalValue(value).internalValue

        private fun fromExternalValue(value: String?): AvvisningsKodeDto =
            value?.let { valuesByExternal[it.uppercase()] } ?: default(value)

        private fun default(externalValue: String?): AvvisningsKodeDto =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Ukjent ekstern avvisningskode '$externalValue'" } }
            else
                UNKNOWN
    }
}