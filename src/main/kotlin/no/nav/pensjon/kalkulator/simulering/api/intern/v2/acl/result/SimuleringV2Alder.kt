package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.general.Alder

data class SimuleringV2Alder(
    @field:Schema(description = "Antall helt fylte år")
    @field:NotNull
    val aar: Int,

    @field:Schema(description = "Antall helt fylte måneder (0..11)")
    @field:NotNull
    val maaneder: Int
) {
    companion object {
        fun transferable(source: Alder) =
            SimuleringV2Alder(
                aar = source.aar,
                maaneder = source.maaneder
            )
    }
}