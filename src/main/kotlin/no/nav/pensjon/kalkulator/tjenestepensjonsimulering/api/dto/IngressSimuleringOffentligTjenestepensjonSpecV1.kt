package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto

import no.nav.pensjon.kalkulator.general.Alder
import java.time.LocalDate

data class IngressSimuleringOffentligTjenestepensjonSpecV1 (
    val foedselsdato: LocalDate,
    val uttaksalder: Alder,
    val aarligInntektFoerUttakBeloep: Int,
    val utenlandsperiodeListe: List<UtenlandsoppholdV1> = emptyList(),
    val epsHarPensjon: Boolean,
    val epsHarInntektOver2G: Boolean,
    val brukerBaOmAfp: Boolean,
)

data class UtenlandsoppholdV1 (
    val fom: LocalDate,
    val tom: LocalDate?
)