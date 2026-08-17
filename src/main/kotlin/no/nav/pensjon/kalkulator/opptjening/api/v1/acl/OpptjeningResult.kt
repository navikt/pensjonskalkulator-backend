package no.nav.pensjon.kalkulator.opptjening.api.v1.acl

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.merknad.MerknadCode

/**
 * NB: Should be kept in sync with SimuleringV1Opptjening
 */
data class OpptjeningV1(
    @field:Schema(description = "Hvilket årstall (kalenderår) som informasjonen gjelder for")
    @field:NotNull
    val aarstall: Int,

    @field:Schema(description = "Årlig pensjonsgivende inntekt (beløp i norske kroner)")
    @field:NotNull
    val pensjonsgivendeInntektBeloep: Int,

    @field:Schema(description = "Opptjente pensjonspoeng")
    @field:NotNull
    val pensjonspoeng: Double,

    @field:Schema(description = "Pensjonsbeholdning (beløp i norske kroner)")
    @field:NotNull
    val pensjonsbeholdningBeloep: Int,

    @field:Schema(description = "Merknader som er knyttet til opptjeningen")
    @field:NotNull
    val merknadListe: List<MerknadCodeV1>
)

enum class MerknadCodeV1(private val internalValue: MerknadCode) {

    AFP(internalValue = MerknadCode.AFP),
    REFORM(internalValue = MerknadCode.REFORM),
    INGEN_OPPTJENING(internalValue = MerknadCode.INGEN_OPPTJENING),
    UFOEREGRAD(internalValue = MerknadCode.UFOEREGRAD),
    DAGPENGER(internalValue = MerknadCode.DAGPENGER),
    FOERSTEGANGSTJENESTE(internalValue = MerknadCode.FOERSTEGANGSTJENESTE),
    OMSORGSOPPTJENING(internalValue = MerknadCode.OMSORGSOPPTJENING),
    GRADERT_UTTAK(internalValue = MerknadCode.GRADERT_UTTAK),
    HELT_UTTAK(internalValue = MerknadCode.HELT_UTTAK),
    // Special value representing missing/unknown value:
    UNKNOWN(internalValue = MerknadCode.UNKNOWN),
    NONE(internalValue = MerknadCode.NONE);

    companion object {
        fun fromInternalValue(value: MerknadCode): MerknadCodeV1 =
            entries.singleOrNull { it.internalValue == value }
                ?: throw IllegalArgumentException("Intern verdi ikke støttet i API v1 - MerknadCode $value")
    }
}