package no.nav.pensjon.kalkulator.simulering.api.dto

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import org.springframework.util.StringUtils.hasLength

/**
 * Incoming (ingress) data transfer object (DTO) containing specification for 'anonym simulering av alderspensjon'.
 * Data-elementer:
 * - Simuleringstype ('Alderspensjon' eller 'Alderspensjon med AFP i privat sektor')
 * - Fødselsår
 * - Sivilstatus
 * - Om ektefelle/partner/samboer har inntekt over 2G
 * - Om ektefelle/partner/samboer har pensjon
 * - Antall år i utlandet
 * - Antall år bruker har hatt over 1G i årlig pensjonsgivende inntekt som yrkesaktiv fram til første uttak av pensjon
 * - Forventet gjennomsnittlig årlig inntekt som yrkesaktiv fram til første uttak av pensjon
 * - Dato/alder for start av gradert uttak
 * - Uttaksgrad
 * - Årlig inntekt under gradert uttak
 * - Dato/alder for start av helt uttak
 * - Årlig inntekt etter start av  helt uttak
 * - Antall år med inntekt etter start av helt uttak
 */
data class AnonymSimuleringSpecV1(
    val simuleringType: String?,
    val foedselAar: Int,
    val sivilstand: String?,
    val epsHarInntektOver2G: Boolean? = false,
    val epsHarPensjon: Boolean? = false,
    val utenlandsAntallAar: Int? = 0,
    val inntektOver1GAntallAar: Int? = 0,
    val aarligInntektFoerUttakBeloep: Int? = 0,
    val gradertUttak: AnonymSimuleringGradertUttakV1? = null, // default is helt uttak (100 %)
    val heltUttak: AnonymSimuleringHeltUttakV1
)

data class AnonymSimuleringGradertUttakV1(
    val grad: Int,
    val uttakAlder: AnonymSimuleringAlderV1,
    val aarligInntektVsaPensjonBeloep: Int?
)

data class AnonymSimuleringHeltUttakV1(
    val uttakAlder: AnonymSimuleringAlderV1,
    val aarligInntektVsaPensjon: AnonymSimuleringInntektV1?
)

data class AnonymSimuleringInntektV1(
    val beloep: Int,
    val sluttAlder: AnonymSimuleringAlderV1
)

data class AnonymSimuleringAlderV1(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}

enum class AnonymSimuleringTypeV1(val externalValue: String, val internalValue: SimuleringType) {
    ALDERSPENSJON("ALDERSPENSJON", SimuleringType.ALDERSPENSJON),
    ALDERSPENSJON_MED_AFP_PRIVAT("ALDERSPENSJON_MED_AFP_PRIVAT", SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT);

    companion object {
        private val values = AnonymSimuleringTypeV1.entries.toTypedArray()
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                ALDERSPENSJON.also { log.warn { "Unknown AnonymSimuleringTypeV1: '$externalValue'" } }
            else
                ALDERSPENSJON
    }
}

enum class AnonymSivilstandV1(val externalValue: String, val internalValue: Sivilstand) {
    UGIFT("UGIFT", Sivilstand.UGIFT),
    GIFT("GIFT", Sivilstand.GIFT),
    SAMBOER("SAMBOER", Sivilstand.SAMBOER);

    companion object {
        private val values = AnonymSivilstandV1.entries.toTypedArray()
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UGIFT.also { log.warn { "Unknown AnonymSivilstandV1: '$externalValue'" } }
            else
                UGIFT
    }
}
