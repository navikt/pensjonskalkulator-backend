package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.simulering.SimuleringResult

data class SimuleringV2Trygdetid(
    @field:Schema(description = "Trygdetid i antall år")
    @field:NotNull
    val antallAar: Int,

    @field:Schema(description = "Hvorvidt trygdetiden er utilstrekkelig (dvs. ikke nok for innvilgelse av pensjon)")
    @field:NotNull
    val erUtilstrekkelig: Boolean
) {
    companion object {
        fun transferable(source: SimuleringResult) =
            SimuleringV2Trygdetid(
                antallAar = source.trygdetid,
                erUtilstrekkelig = source.harForLiteTrygdetid
            )
    }
}