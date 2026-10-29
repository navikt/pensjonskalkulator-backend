package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.tech.web.BadRequestException

data class SimuleringV2GradertUttakSpec(
    @field:Schema(description = "Uttaksgrad i prosent")
    @field:NotNull
    val grad: Int,

    @field:Schema(description = "Personens alder (fra og med) ved start av gradert uttak (20–80 %)")
    @field:NotNull
    val uttaksalder: SimuleringV2AlderSpec,

    @field:Schema(description = "Årlig inntekt ved siden av pensjon (beløp i norske kroner)")
    val aarligInntektVsaPensjonBeloep: Int? = null // Vsa = ved siden av
) {
    fun toInternalValue() =
        GradertUttak(
            grad = uttaksgrad(),
            uttakFomAlder = uttaksalder.toInternalValue(),
            aarligInntekt = aarligInntektVsaPensjonBeloep ?: 0
        )

    private fun uttaksgrad(): Uttaksgrad =
        try {
            Uttaksgrad.from(prosentsats = grad)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Ugyldig grad for gradert uttak $grad", e)
        }
}