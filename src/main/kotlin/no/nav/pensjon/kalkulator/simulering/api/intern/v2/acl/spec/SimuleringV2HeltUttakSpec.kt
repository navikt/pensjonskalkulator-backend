package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.general.HeltUttak

data class SimuleringV2HeltUttakSpec(
    @field:Schema(description = "Personens alder (fra og med) ved start av helt uttak (100 %)")
    @field:NotNull
    val uttaksalder: SimuleringV2AlderSpec,

    @field:Schema(description = "Årlig inntekt ved siden av pensjon (beløp i norske kroner)")
    val aarligInntektVsaPensjon: SimuleringV2InntektSpec? = null // Vsa = ved siden av
) {
    fun toInternalValue() =
        HeltUttak(
            uttakFomAlder = uttaksalder.toInternalValue(),
            inntekt = aarligInntektVsaPensjon?.toInternalValue()
        )
}