package no.nav.pensjon.kalkulator.opptjening

/**
 * Årlig opptjening av pensjonsrettigheter.
 */
data class AarligOpptjening(
    val aar: Int,
    val pensjonsgivendeInntekt: Int,
    val pensjonspoeng: Double,
    val omsorgspoeng: Int?,
    val maksimalUfoeregrad: Int?,
    val pensjonspoengType: String
    //TODO use enum for pensjonspoengType?
// ref. https://github.com/navikt/popp/blob/main/popp-domain/src/main/java/no/nav/popp/domain/codestable/PensjonspoengTypeCode.java
)