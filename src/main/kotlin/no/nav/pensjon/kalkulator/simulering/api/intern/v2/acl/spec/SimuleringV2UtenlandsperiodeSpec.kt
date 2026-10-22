package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.simulering.Opphold
import java.time.LocalDate

data class SimuleringV2UtenlandsperiodeSpec(
    @field:NotNull val fom: LocalDate,
    val tom: LocalDate? = null,
    @field:NotNull val landkode: String,
    @field:NotNull val arbeidetUtenlands: Boolean
) {
    fun toInternalValue() =
        Opphold(
            fom = fom,
            tom = tom,
            land = Land.valueOf(landkode), // NB: Must be corresponding 1-to-1
            arbeidet = arbeidetUtenlands
        )
}