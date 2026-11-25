package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.acl

import no.nav.pensjon.kalkulator.common.api.EnumUtil.handleMissingExternalValue
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.AvvisningAarsak

enum class AvvisningAarsakDto(val internalValue: AvvisningAarsak) {
    STRENGT_FORTROLIG_ADRESSE(internalValue = AvvisningAarsak.STRENGT_FORTROLIG_ADRESSE),
    STRENGT_FORTROLIG_UTLAND(internalValue = AvvisningAarsak.STRENGT_FORTROLIG_UTLAND),
    AVDOED(internalValue = AvvisningAarsak.AVDOED),
    VERGEMAAL(internalValue = AvvisningAarsak.VERGEMAAL),
    PERSON_UTLAND(internalValue = AvvisningAarsak.PERSON_UTLAND),
    SKJERMING(internalValue = AvvisningAarsak.SKJERMING),
    FORTROLIG_ADRESSE(internalValue = AvvisningAarsak.FORTROLIG_ADRESSE),
    UKJENT_BOSTED(internalValue = AvvisningAarsak.UKJENT_BOSTED),
    GEOGRAFISK(internalValue = AvvisningAarsak.GEOGRAFISK),
    HABILITET(internalValue = AvvisningAarsak.HABILITET),
    MANGLENDE_FAGGRUPPE_MEDLEMSKAP(internalValue = AvvisningAarsak.MANGLENDE_FAGGRUPPE_MEDLEMSKAP),
    POPULASJONSTILGANGSSJEKK_FEIL(internalValue = AvvisningAarsak.POPULASJONSTILGANGSSJEKK_FEIL),
    TILGANGSSJEKK_FEIL(internalValue = AvvisningAarsak.TILGANGSSJEKK_FEIL),
    UNKNOWN(internalValue = AvvisningAarsak.UNKNOWN);

    companion object {
        val valuesByInternal = entries.associateBy { it.internalValue }

        fun fromInternalValue(value: AvvisningAarsak?): AvvisningAarsakDto =
            (value ?: UNKNOWN).let { valuesByInternal[it] }
                ?: handleMissingExternalValue(apiId = "security.ingress.impersonal", type = "avvisningsårsak", value)
    }
}