package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.simulering.LevendeEps

data class SimuleringV2LevendeEpsSpec(
    @field:Schema(description = "Hvorvidt ektefellen/partneren/samboeren har inntekt over 2G (2 ganger grunnbeløpet)")
    @field:NotNull
    val harInntektOver2G: Boolean, // 2G = 2 ganger grunnbeløpet

    @field:Schema(description = "Hvorvidt ektefellen/partneren/samboeren har startet uttak av pensjon")
    @field:NotNull
    val harPensjon: Boolean
) {
    fun toInternalValue() =
        LevendeEps(
            harInntektOver2G = harInntektOver2G,
            harPensjon = harPensjon
        )
}