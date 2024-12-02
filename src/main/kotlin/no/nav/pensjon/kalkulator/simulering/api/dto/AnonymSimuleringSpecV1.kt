package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType

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
    val simuleringstype: AnonymSimuleringTypeV1?,
    val foedselAar: Int,
    val sivilstand: AnonymSivilstandV1?,
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
    val uttaksalder: AnonymSimuleringAlderV1,
    val aarligInntektVsaPensjonBeloep: Int?
)

data class AnonymSimuleringHeltUttakV1(
    val uttaksalder: AnonymSimuleringAlderV1,
    val aarligInntektVsaPensjon: AnonymSimuleringInntektV1?
)

data class AnonymSimuleringInntektV1(
    val beloep: Int,
    val sluttAlder: AnonymSimuleringAlderV1?
)

data class AnonymSimuleringAlderV1(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}

enum class AnonymSimuleringTypeV1(val externalValue: SimuleringType) {
    ALDERSPENSJON(SimuleringType.ALDERSPENSJON),
    ALDERSPENSJON_MED_AFP_PRIVAT(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT);
}

enum class AnonymSivilstandV1(val externalValue: Sivilstand) {
    UGIFT(Sivilstand.UGIFT),
    GIFT(Sivilstand.GIFT),
    SAMBOER(Sivilstand.SAMBOER);
}
