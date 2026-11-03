package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.simulering.SimulertOpptjening

data class SimuleringV2AarligBeloep(
    @field:Schema(description = "Hvilket årstall (kalenderår) som informasjonen gjelder for")
    @field:NotNull
    val aarstall: Int,

    @field:NotNull val beloep: Int
) {
    companion object {
        fun transferable(source: SimulertOpptjening) =
            SimuleringV2AarligBeloep(
                aarstall = source.aarstall,
                beloep = source.pensjonsgivendeInntektBeloep
            )
    }
}