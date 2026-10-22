package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.simulering.SimulertAfpPrivat

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimuleringV2PrivatAfp(
    @field:Schema(description = "Personens alder (år) som informasjonen gjelder for")
    @field:NotNull
    val alderAar: Int,

    @field:Schema(description = "Årlig beløp (beløp i norske kroner)")
    @field:NotNull
    val aarligBeloep: Int,

    @field:Schema(description = "Kompensasjonstillegg (beløp i norske kroner)")
    @field:NotNull
    val kompensasjonstillegg: Int,

    @field:Schema(description = "Kronetillegg (beløp i norske kroner)")
    @field:NotNull
    val kronetillegg: Int,

    @field:Schema(description = "Livsvarig del (beløp i norske kroner)")
    @field:NotNull
    val livsvarig: Int,

    @field:Schema(description = "Månedlig beløp (beløp i norske kroner)")
    val maanedligBeloep: Int?
) {
    companion object {
        fun transferable(source: SimulertAfpPrivat) =
            SimuleringV2PrivatAfp(
                alderAar = source.alder,
                aarligBeloep = source.beloep,
                kompensasjonstillegg = source.kompensasjonstillegg,
                kronetillegg = source.kronetillegg,
                livsvarig = source.livsvarig,
                maanedligBeloep = source.maanedligBeloep
            )
    }
}