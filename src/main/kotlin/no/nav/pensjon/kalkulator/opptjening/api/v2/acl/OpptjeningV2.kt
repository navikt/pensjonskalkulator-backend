package no.nav.pensjon.kalkulator.opptjening.api.v2.acl

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening

/**
 * NB: Should be kept in sync with SimuleringV2Opptjening
 */
data class OpptjeningV2(
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
    val merknadListe: List<MerknadV2>
) {
    companion object {
        /**
         * Maps from domain representation to transferable representation (data transfer object).
         */
        fun transferable(opptjening: AarligOpptjening) =
            OpptjeningV2(
                aarstall = opptjening.aar,
                pensjonsgivendeInntektBeloep = opptjening.pensjonsgivendeInntekt,
                pensjonspoeng = opptjening.pensjonspoeng,
                pensjonsbeholdningBeloep = opptjening.beholdning,
                merknadListe = opptjening.merknadListe.map(MerknadV2::transferable)
            )
    }
}