package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.general.Inntekt

data class SimuleringV2InntektSpec(
    @field:Schema(description = "Årlig inntekt (beløp i norske kroner)")
    @field:NotNull
    val beloep: Int,

    @field:Schema(description = "Alder (til men ikke med) ved opphør av inntekt")
    @field:NotNull
    val sluttAlder: SimuleringV2AlderSpec
) {
    fun toInternalValue() =
        Inntekt(
            aarligBeloep = beloep,
            tomAlder = sluttAlder.toInternalValue()
        )
}