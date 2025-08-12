package no.nav.pensjon.kalkulator.tjenestepensjonsimulering

import no.nav.pensjon.kalkulator.general.LoependeInntekt
import java.time.LocalDate

data class SimuleringOffentligTjenestepensjonSpec(
    val foedselsdato: LocalDate,
    val uttaksdato: LocalDate,
    val sisteInntekt: Int,
    val fremtidigeInntekter: List<LoependeInntekt>,
    val aarIUtlandetEtter16: Int,
    val brukerBaOmAfp: Boolean,
    val epsPensjon: Boolean,
    val eps2G: Boolean,
    val erApoteker: Boolean,
)
