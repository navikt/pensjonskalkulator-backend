package no.nav.pensjon.kalkulator.opptjening.api.v1.acl

import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening

object OpptjeningResultMapper {

    fun toDto(opptjening: AarligOpptjening) =
        OpptjeningV1(
            aarstall = opptjening.aar,
            pensjonsgivendeInntektBeloep = opptjening.pensjonsgivendeInntekt,
            pensjonspoeng = opptjening.pensjonspoeng,
            pensjonsbeholdningBeloep = opptjening.beholdning
        )
}