package no.nav.pensjon.kalkulator.opptjening.api.v1.acl

import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening

object OpptjeningResultMapper {

    fun toDto(source: List<AarligOpptjening>) =
        OpptjeningV1Result(
            opptjeningListe = source.map(::opptjening)
        )

    private fun opptjening(source: AarligOpptjening) =
        OpptjeningV1(
            aar = source.aar,
            pensjonsgivendeInntekt = source.pensjonsgivendeInntekt,
            pensjonspoeng = source.pensjonspoeng,
            omsorgspoeng = source.omsorgspoeng,
            pensjonspoengType = source.pensjonspoengType
        )
}