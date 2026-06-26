package no.nav.pensjon.kalkulator.opptjening

import no.nav.pensjon.kalkulator.general.Aarlig

/**
 * Årlig opptjening av pensjonsrettigheter.
 */
data class AarligOpptjening(
    override val aar: Int,
    val pensjonsgivendeInntekt: Int,
    val pensjonspoeng: Double,
    val omsorgspoeng: Int?,
    val maksimalUfoeregrad: Int?,
    val pensjonspoengType: String,
    val beholdning: Int
    //TODO use enum for pensjonspoengType?
// ref. https://github.com/navikt/popp/blob/main/popp-domain/src/main/java/no/nav/popp/domain/codestable/PensjonspoengTypeCode.java
): Aarlig {
    fun withBeholdning(beholdning: Int) =
        copy(beholdning = beholdning)
}