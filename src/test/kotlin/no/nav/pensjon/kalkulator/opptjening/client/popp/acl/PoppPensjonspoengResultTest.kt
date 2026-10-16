package no.nav.pensjon.kalkulator.opptjening.client.popp.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import no.nav.pensjon.kalkulator.opptjening.PensjonspoengType

class PoppPensjonspoengResultTest : ShouldSpec({

    should("map data transfer object to domain object") {
        PoppPensjonspoengResult(
            pensjonspoeng = listOf(
                PoppPensjonspoeng(
                    pensjonspoengId = 1L, // mappes ikke
                    fnr = pid.value, // ditto
                    fnrOmsorgFor = "22925399748", // ditto
                    kilde = "K1", // ditto
                    pensjonspoengType = "PPI",
                    inntekt = PoppPensjonspoengInntekt(
                        inntektId = 1, // mappes ikke
                        fnr = pid.value, // ditto
                        inntektAr = 2001, // ditto
                        kilde = "K2", // ditto
                        kommune = "Oslo", // ditto
                        piMerke = "M", // ditto
                        inntektType = "I", // ditto
                        belop = 11L
                    ),
                    omsorg = PoppPensjonspoengOmsorg(
                        omsorgId = 21L, // mappes ikke
                        ar = 2002,
                        omsorgType = "O", // mappes ikke
                    ),
                    ar = 2000,
                    anvendtPi = 2, // mappes ikke
                    poeng = 3.1,
                    maxUforegrad = 4
                )
            )
        ).toInternalValue() shouldBe listOf(
            AarligOpptjening(
                aar = 2000,
                pensjonsgivendeInntekt = 11,
                pensjonspoeng = 3.1,
                maksimalUfoeregrad = 4,
                pensjonspoengType = PensjonspoengType.PENSJONSPOENG_FOR_PENSJONSGIVENDE_INNTEKT,
                beholdning = 0, // mappes ikke i denne sammenheng
                merknadListe = emptyList() // ditto
            )
        )
    }

    should("map minimal data transfer object to domain object") {
        PoppPensjonspoengResult(
            pensjonspoeng = listOf(
                PoppPensjonspoeng(
                    ar = 0,
                    inntekt = PoppPensjonspoengInntekt(),
                    omsorg = PoppPensjonspoengOmsorg()
                )
            )
        ).toInternalValue() shouldBe listOf(
            AarligOpptjening(
                aar = 0,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                maksimalUfoeregrad = null,
                pensjonspoengType = PensjonspoengType.UNKNOWN,
                beholdning = 0,
                merknadListe = emptyList()
            )
        )
    }
})