package no.nav.pensjon.kalkulator.normalder

import no.nav.pensjon.kalkulator.general.Alder

/**
 * Aldersgrenser som gjelder for et gitt årskull.
 */
data class Aldersgrenser(
    val aarskull: Int,
    val nedreAlder: Alder,
    val normalder: Alder,
    val oevreAlder: Alder,
    val verdiStatus: VerdiStatus
)

/**
 * Angir hvorvidt en verdi er fast bestemt eller prognosert (estimert framtidig verdi).
 */
enum class VerdiStatus {
    FAST,
    PROGNOSE
}
