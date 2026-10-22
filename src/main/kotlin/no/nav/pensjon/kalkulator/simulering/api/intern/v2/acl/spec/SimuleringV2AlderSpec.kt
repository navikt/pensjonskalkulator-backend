package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.general.Alder

data class SimuleringV2AlderSpec(
    @field:Schema(description = "Antall helt fylte år")
    @field:NotNull
    val aar: Int,

    @field:Schema(description = "Antall helt fylte måneder (0..11)")
    @field:NotNull
    val maaneder: Int
) {
    fun toInternalValue() =
        Alder(aar = aar, maaneder = maaneder)
}