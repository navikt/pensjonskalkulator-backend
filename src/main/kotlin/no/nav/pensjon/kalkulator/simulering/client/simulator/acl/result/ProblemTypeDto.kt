package no.nav.pensjon.kalkulator.simulering.client.simulator.acl.result

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.validity.ProblemType
import org.springframework.util.StringUtils.hasLength

enum class ProblemTypeDto(val internalValue: ProblemType) {
    UGYLDIG_UTTAKSDATO(internalValue = ProblemType.UGYLDIG_UTTAKSDATO),
    UGYLDIG_UTTAKSGRAD(internalValue = ProblemType.UGYLDIG_UTTAKSGRAD),
    UGYLDIG_SIVILSTATUS(internalValue = ProblemType.UGYLDIG_SIVILSTATUS),
    UGYLDIG_INNTEKT(internalValue = ProblemType.UGYLDIG_INNTEKT),
    UGYLDIG_ANTALL_AAR(internalValue = ProblemType.UGYLDIG_ANTALL_AAR),
    UGYLDIG_PERSONIDENT(internalValue = ProblemType.UGYLDIG_PERSONIDENT),
    PERSON_IKKE_FUNNET(internalValue = ProblemType.PERSON_IKKE_FUNNET),
    PERSON_FOR_HOEY_ALDER(internalValue = ProblemType.PERSON_FOR_HOEY_ALDER),
    UTILSTREKKELIG_OPPTJENING(internalValue = ProblemType.UTILSTREKKELIG_OPPTJENING),
    UTILSTREKKELIG_TRYGDETID(internalValue = ProblemType.UTILSTREKKELIG_TRYGDETID),
    ANNEN_KLIENTFEIL(internalValue = ProblemType.ANNEN_KLIENTFEIL),
    SERVERFEIL(internalValue = ProblemType.SERVERFEIL);

    companion object {
        private val values = entries.toTypedArray()
        private val defaultValue = SERVERFEIL
        private val log = KotlinLogging.logger {}

        fun internalValue(value: String?) =
            fromExternalValue(value).internalValue

        private fun fromExternalValue(value: String?) =
            values.singleOrNull { it.name.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                defaultValue.also { log.warn { "Ekstern verdi ikke støttet - pensjonssimulator returnerte problemtype '$externalValue'" } }
            else
                defaultValue
    }
}