package no.nav.pensjon.kalkulator.opptjening.api.v1.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.merknad.Merknad
import no.nav.pensjon.kalkulator.merknad.MerknadCode
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import no.nav.pensjon.kalkulator.opptjening.PensjonspoengType

class OpptjeningResultMapperTest : ShouldSpec({

    should("map domain object to data transfer object, excluding uføregrad/omsorgspoeng/pensjonspoengType") {
        OpptjeningResultMapper.transferable(
            opptjening = AarligOpptjening(
                aar = 2021,
                pensjonsgivendeInntekt = 1,
                pensjonspoeng = 2.1,
                maksimalUfoeregrad = 4, // not mapped
                pensjonspoengType = PensjonspoengType.PENSJONSPOENG_FOR_PENSJONSGIVENDE_INNTEKT, // not mapped
                beholdning = 12,
                merknadListe = listOf(
                    Merknad(kode = MerknadCode.AFP),
                    Merknad(kode = MerknadCode.DAGPENGER)
                )
            ),
        ) shouldBe OpptjeningV1(
            aarstall = 2021,
            pensjonsgivendeInntektBeloep = 1,
            pensjonspoeng = 2.1,
            pensjonsbeholdningBeloep = 12,
            merknadListe = listOf(
                MerknadCodeV1.AFP,
                MerknadCodeV1.DAGPENGER
            )
        )
    }
})