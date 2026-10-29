package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.simulering.SimulertOpptjening

data class SimuleringV2Opptjening(
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
    val merknadListe: List<SimuleringV2Merknad>
) {
    companion object {
        fun transferable(source: SimulertOpptjening) =
            SimuleringV2Opptjening(
                aarstall = source.aarstall,
                pensjonsgivendeInntektBeloep = source.pensjonsgivendeInntektBeloep,
                pensjonspoeng = source.pensjonspoeng,
                pensjonsbeholdningBeloep = source.pensjonsbeholdningBeloep,
                merknadListe = source.merknadListe.map(SimuleringV2Merknad::transferable)
            )
    }
}