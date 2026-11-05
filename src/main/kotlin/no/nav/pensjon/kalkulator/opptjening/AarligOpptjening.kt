package no.nav.pensjon.kalkulator.opptjening

import no.nav.pensjon.kalkulator.general.Aarlig
import no.nav.pensjon.kalkulator.opptjening.behandlet.Merknad

/**
 * Årlig opptjening av pensjonsrettigheter.
 */
data class AarligOpptjening(
    override val aar: Int,
    val pensjonsgivendeInntekt: Int,
    val pensjonspoeng: Double,
    val maksimalUfoeregrad: Int?,
    val pensjonspoengType: PensjonspoengType,
    val beholdning: Int,
    val merknadListe: List<Merknad>
) : Aarlig {
    fun medBeholdningOgMerknader(beholdningBeloep: Int, merknadListe: List<Merknad>) =
        copy(
            beholdning = beholdningBeloep,
            merknadListe = merknadListe
        )
}