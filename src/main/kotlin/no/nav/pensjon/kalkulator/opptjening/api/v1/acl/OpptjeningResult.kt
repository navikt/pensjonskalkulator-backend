package no.nav.pensjon.kalkulator.opptjening.api.v1.acl

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

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
    val pensjonsbeholdningBeloep: Int
)