package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.tech.web.BadRequestException
import java.time.LocalDate

data class SimuleringV2UtenlandsperiodeSpec(
    @field:Schema(description = "Utenlandsperiodens startdato (fra og med)")
    @field:NotNull
    val fom: LocalDate,

    @field:Schema(description = "Utenlandsperiodens sluttdato (til og med)")
    val tom: LocalDate? = null,

    @field:Schema(description = "Kode som identifiserer landet")
    @field:NotNull
    val landkode: String,

    @field:Schema(description = "Hvorvidt personen arbeidet i det angitte landet")
    @field:NotNull
    val arbeidetUtenlands: Boolean
) {
    fun toInternalValue() =
        Opphold(
            fom = fom,
            tom = tom,
            land = land(),
            arbeidet = arbeidetUtenlands
        )

    private fun land(): Land =
        try {
            Land.valueOf(landkode) // NB: Must be corresponding 1-to-1
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Ugyldig landkode for opphold $landkode", e)
        }
}