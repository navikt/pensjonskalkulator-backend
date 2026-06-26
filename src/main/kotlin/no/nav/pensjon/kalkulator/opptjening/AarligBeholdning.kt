package no.nav.pensjon.kalkulator.opptjening

import no.nav.pensjon.kalkulator.general.Aarlig

/**
 * Årlig pensjonsbeholdning.
 */
data class AarligBeholdning(
    override val aar: Int,
    val beholdning: Int
): Aarlig