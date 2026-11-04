package no.nav.pensjon.kalkulator.opptjening.behandlet

/**
 * Inneholder pensjonsbeholdning og merknader.
 */
data class BeholdningOgMerknader(
    val beholdning: Beholdning,
    val merknadListe: List<Merknad>
) {
    fun erRelevant(): Boolean =
        beholdning.beloep != 0 || merknadListe.isNotEmpty()
}