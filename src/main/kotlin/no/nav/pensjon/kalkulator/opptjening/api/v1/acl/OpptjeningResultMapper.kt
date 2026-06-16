package no.nav.pensjon.kalkulator.opptjening.api.v1.acl

import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening

object OpptjeningResultMapper {

    fun toDto(opptjening: AarligOpptjening) =
        OpptjeningV1(
            aar = opptjening.aar,
            pensjonsgivendeInntekt = opptjening.pensjonsgivendeInntekt,
            pensjonspoeng = opptjening.pensjonspoeng,
            omsorgspoeng = opptjening.omsorgspoeng,
            beholdning = opptjening.beholdning
        )
}