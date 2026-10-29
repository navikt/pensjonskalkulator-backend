package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.simulering.SimulertAfpOffentlig

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimuleringV2AldersbestemtUtbetaling(
    @field:Schema(description = "Personens alder (år) som informasjonen gjelder for")
    @field:NotNull
    val alderAar: Int,

    @field:Schema(description = "Årlig beløp")
    @field:NotNull val aarligBeloep: Int,
    val maanedligBeloep: Int?
) {
    companion object {
        fun transferable(source: SimulertAfpOffentlig) =
            SimuleringV2AldersbestemtUtbetaling(
                alderAar = source.alder,
                aarligBeloep = source.beloep,
                maanedligBeloep = source.maanedligBeloep
            )
    }
}