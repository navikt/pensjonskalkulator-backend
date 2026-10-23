package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import no.nav.pensjon.kalkulator.simulering.SimulertMaanedligAlderspensjonForKnekkpunkter

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimuleringV2MaanedligAlderspensjonForKnekkpunkter(
    @field:Schema(description = "Månedlig alderspensjon ved gradert uttak")
    val vedGradertUttak: SimuleringV2MaanedligAlderspensjon?,

    @field:Schema(description = "Månedlig alderspensjon ved helt uttak")
    val vedHeltUttak: SimuleringV2MaanedligAlderspensjon?,

    @field:Schema(description = "Månedlig alderspensjon ved normert pensjonsalder")
    val vedNormertPensjonsalder: SimuleringV2MaanedligAlderspensjon?
) {
    companion object {
        fun transferable(source: SimulertMaanedligAlderspensjonForKnekkpunkter) =
            SimuleringV2MaanedligAlderspensjonForKnekkpunkter(
                vedGradertUttak = source.vedGradertUttak?.let(SimuleringV2MaanedligAlderspensjon::transferable),
                vedHeltUttak = source.vedHeltUttak?.let(SimuleringV2MaanedligAlderspensjon::transferable),
                vedNormertPensjonsalder = source.vedNormertPensjonsalder?.let(SimuleringV2MaanedligAlderspensjon::transferable)
            )
    }
}