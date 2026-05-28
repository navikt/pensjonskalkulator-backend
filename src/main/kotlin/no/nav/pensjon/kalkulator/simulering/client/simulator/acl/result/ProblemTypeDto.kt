package no.nav.pensjon.kalkulator.simulering.client.simulator.acl.result

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.validity.ProblemType
import org.springframework.util.StringUtils.hasLength

/**
 * Corresponds to the following class in pensjonssimulator:
 * no.nav.pensjon.simulator.api.nav.v2.acl.result.ProblemTypeDto
 */
enum class ProblemTypeDto(val internalValue: ProblemType) {
    UGYLDIG_UTTAKSDATO(internalValue = ProblemType.UGYLDIG_UTTAKSDATO),
    UGYLDIG_UTTAKSGRAD(internalValue = ProblemType.UGYLDIG_UTTAKSGRAD),
    UGYLDIG_SIVILSTATUS(internalValue = ProblemType.UGYLDIG_SIVILSTATUS),
    UGYLDIG_INNTEKT(internalValue = ProblemType.UGYLDIG_INNTEKT),
    UGYLDIG_ANTALL_AAR(internalValue = ProblemType.UGYLDIG_ANTALL_AAR),
    UGYLDIG_PERSONIDENT(internalValue = ProblemType.UGYLDIG_PERSONIDENT),
    PERSON_IKKE_FUNNET(internalValue = ProblemType.PERSON_IKKE_FUNNET),
    PERSON_FOR_LAV_ALDER(internalValue = ProblemType.PERSON_FOR_LAV_ALDER),
    PERSON_FOR_HOEY_ALDER(internalValue = ProblemType.PERSON_FOR_HOEY_ALDER),
    UTILSTREKKELIG_INNTEKT(internalValue = ProblemType.UTILSTREKKELIG_INNTEKT),
    UTILSTREKKELIG_OPPTJENING(internalValue = ProblemType.UTILSTREKKELIG_OPPTJENING),
    UTILSTREKKELIG_TRYGDETID(internalValue = ProblemType.UTILSTREKKELIG_TRYGDETID),
    ANNEN_KLIENTFEIL(internalValue = ProblemType.ANNEN_KLIENTFEIL),
    INTERN_DATA_INKONSISTENS(internalValue = ProblemType.INTERN_DATA_INKONSISTENS),
    IMPLEMENTASJONSFEIL(internalValue = ProblemType.IMPLEMENTASJONSFEIL),
    TREDJEPARTSFEIL(internalValue = ProblemType.TREDJEPARTSFEIL),
    ANNEN_SERVERFEIL(internalValue = ProblemType.ANNEN_SERVERFEIL);

    companion object {
        private val defaultValue = ANNEN_SERVERFEIL
        private val log = KotlinLogging.logger {}

        fun internalValue(value: String?) =
            fromExternalValue(value).internalValue

        private fun fromExternalValue(value: String?) =
            entries.singleOrNull { it.name.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                defaultValue.also { log.warn { "Ekstern verdi ikke støttet - pensjonssimulator returnerte problemtype '$externalValue'" } }
            else
                defaultValue
    }
}