package no.nav.pensjon.kalkulator.opptjening.api.v1.acl

import no.nav.pensjon.kalkulator.merknad.MerknadCode
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening

object OpptjeningResultMapper {

    /**
     * Maps from domain representation to transferable representation (data transfer object).
     */
    fun transferable(opptjening: AarligOpptjening) =
        OpptjeningV1(
            aarstall = opptjening.aar,
            pensjonsgivendeInntektBeloep = opptjening.pensjonsgivendeInntekt,
            pensjonspoeng = opptjening.pensjonspoeng,
            pensjonsbeholdningBeloep = opptjening.beholdning,
            merknadListe = opptjening.merknadListe.mapNotNull { transferable(it.kode) }
        )

    private fun transferable(kode: MerknadCode): MerknadCodeV1? =
        MerknadCodeV1.fromInternalValue(kode).let {
            if (it == MerknadCodeV1.UNKNOWN) null else it
        }
}