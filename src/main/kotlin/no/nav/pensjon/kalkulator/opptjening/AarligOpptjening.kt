package no.nav.pensjon.kalkulator.opptjening

import no.nav.pensjon.kalkulator.general.Aarlig
import no.nav.pensjon.kalkulator.merknad.MerknadCode

/**
 * Årlig opptjening av pensjonsrettigheter.
 */
data class AarligOpptjening(
    override val aar: Int,
    val pensjonsgivendeInntekt: Int,
    val pensjonspoeng: Double,
    val omsorgspoeng: Int?,
    val maksimalUfoeregrad: Int?,
    val pensjonspoengType: PensjonspoengType,
    val beholdning: Int,
    val merknadListe: List<MerknadCode>
) : Aarlig {
    fun withBeholdning(beholdning: Int) =
        copy(beholdning = beholdning)

    fun withMerknadListe(merknadListe: List<MerknadCode>) =
        copy(merknadListe = merknadListe)
}