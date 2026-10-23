package no.nav.pensjon.kalkulator.opptjening.api.v2.acl

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.merknad.UttaksgradMerknad

data class UttaksgradV2(
    @field:Schema(description = "Uttaksgrad angitt i prosent")
    @field:NotNull
    val grad: Int,

    @field:Schema(description = "Hvilken kalendermåned (1..12) uttaket med denne graden startet (fra og med)")
    @field:NotNull
    val kalendermaanedFom: Int
) {
    companion object {
        /**
         * Maps from domain representation to transferable representation (data transfer object).
         */
        fun transferable(source: UttaksgradMerknad) =
            UttaksgradV2(
                grad = source.grad,
                kalendermaanedFom = source.kalendermaanedFom
            )
    }
}