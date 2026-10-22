package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import no.nav.pensjon.kalkulator.afp.BeregnetAfp

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimuleringV2ServiceberegnetAfp(
    @field:Schema(description = "Beregnet avtalefestet pensjon")
    val beregnetAfp: SimuleringV2BeregnetAfp?
) {
    companion object {
        fun transferable(source: BeregnetAfp) =
            SimuleringV2ServiceberegnetAfp(
                beregnetAfp = SimuleringV2BeregnetAfp.transferable(source)
            )
    }
}