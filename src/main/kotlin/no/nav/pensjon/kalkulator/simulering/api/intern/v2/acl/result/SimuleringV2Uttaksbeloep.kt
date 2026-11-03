package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.simulering.AlderspensjonMaanedsbeloep

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimuleringV2Uttaksbeloep(
    @field:Schema(description = "Månedlig pensjonsbeløp ved start av gradert uttak")
    val gradertUttakMaanedligBeloep: Int? = null,

    @field:Schema(description = "Månedlig pensjonsbeløp ved start av helt uttak")
    @field:NotNull
    val heltUttakMaanedligBeloep: Int,
) {
    companion object {
        fun transferable(source: AlderspensjonMaanedsbeloep) =
            SimuleringV2Uttaksbeloep(
                gradertUttakMaanedligBeloep = source.gradertUttak,
                heltUttakMaanedligBeloep = source.heltUttak
            )
    }
}