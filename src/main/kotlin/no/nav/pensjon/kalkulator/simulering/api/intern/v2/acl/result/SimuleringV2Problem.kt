package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.validity.Problem

data class SimuleringV2Problem(
    @field:Schema(description = "Type problem")
    @field:NotNull
    val kode: SimuleringV2ProblemType,

    @field:Schema(description = "Fritekstlig beskrivelse av problemet")
    @field:NotNull
    val beskrivelse: String
) {
    companion object {
        fun transferable(source: Problem) =
            SimuleringV2Problem(
                kode = SimuleringV2ProblemType.entries.firstOrNull { it.internalValue == source.type }
                    ?: SimuleringV2ProblemType.SERVERFEIL,
                beskrivelse = source.beskrivelse
            )

        fun serverfeil(e: Exception) =
            SimuleringV2Problem(
                kode = SimuleringV2ProblemType.SERVERFEIL,
                beskrivelse = e.message ?: e.javaClass.simpleName
            )
    }
}