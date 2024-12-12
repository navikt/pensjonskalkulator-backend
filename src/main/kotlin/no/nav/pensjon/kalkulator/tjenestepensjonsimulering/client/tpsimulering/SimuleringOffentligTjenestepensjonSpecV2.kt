package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering

import java.time.LocalDate

data class SimuleringOffentligTjenestepensjonSpecV2(
    val foedselsdato: LocalDate,
    val uttaksdato: LocalDate,
    val sisteInntekt: Int,
    val fremtidigeInntekter: List<FremtidigInntektV2>,
    val aarIUtlandetEtter16: Int,
    val brukerBaOmAfp: Boolean,
    val epsPensjon: Boolean,
    val eps2G: Boolean,
)

data class FremtidigInntektV2(val fom: LocalDate, val beloep: Int)
