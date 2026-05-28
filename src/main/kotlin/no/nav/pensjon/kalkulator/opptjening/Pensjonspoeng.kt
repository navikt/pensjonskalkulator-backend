package no.nav.pensjon.kalkulator.opptjening

data class Pensjonspoeng(
    val ar: Int,
    val pensjonsgivendeInntekt: Int,
    val pensjonspoeng: Double,
    val omsorgspoeng: Int?,
    val maksUforegrad: Int?,
    val pensjonspoengType: String
)
