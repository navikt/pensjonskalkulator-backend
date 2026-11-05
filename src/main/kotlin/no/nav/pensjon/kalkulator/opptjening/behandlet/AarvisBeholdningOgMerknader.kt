package no.nav.pensjon.kalkulator.opptjening.behandlet

/**
 * Inneholder pensjonsbeholdning og merknader for hvert opptjeningsår.
 */
data class AarvisBeholdningOgMerknader(
    val perAar: Map<Int, BeholdningOgMerknader>
)