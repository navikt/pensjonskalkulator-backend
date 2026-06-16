package no.nav.pensjon.kalkulator.opptjening.api.v1.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening

class OpptjeningResultMapperTest : ShouldSpec({

    should("map domain object to data transfer object, excluding uføregrad & pensjonspoengType") {
        OpptjeningResultMapper.toDto(
            opptjening = AarligOpptjening(
                aar = 2021,
                pensjonsgivendeInntekt = 1,
                pensjonspoeng = 2.1,
                omsorgspoeng = 3,
                maksimalUfoeregrad = 4, // not mapped
                pensjonspoengType = "T1", // not mapped
                beholdning = 12
            ),
        ) shouldBe OpptjeningV1(
            aar = 2021,
            pensjonsgivendeInntekt = 1,
            pensjonspoeng = 2.1,
            omsorgspoeng = 3,
            beholdning = 12
        )
    }
})