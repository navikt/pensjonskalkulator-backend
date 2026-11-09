package no.nav.pensjon.kalkulator.simulering.client.simulator.acl.result

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.simulering.SimulertTidsbegrensetOffentligAfp

class TidsbegrensetOffentligAfpDtoTest : ShouldSpec({

    should("konvertere eksternt bestemt objektformat til intern domenerepresentasjon") {
        TidsbegrensetOffentligAfpDto(
            alderAar = 65,
            totaltAfpBeloep = 1,
            tidligereArbeidsinntekt = 2,
            grunnbeloep = 3,
            sluttpoengtall = 4.5,
            trygdetid = 5,
            poengaarTom1991 = 6,
            poengaarFom1992 = 7,
            grunnpensjon = 8,
            tilleggspensjon = 9,
            afpTillegg = 10,
            saertillegg = 11,
            afpGrad = 12,
            erAvkortet = true,
            fremtidigePensjonspoeng = 13.4
        ).toInternalValue() shouldBe SimulertTidsbegrensetOffentligAfp(
            alderAar = 65,
            totaltAfpBeloep = 1,
            tidligereArbeidsinntekt = 2,
            grunnbeloep = 3,
            sluttpoengtall = 4.5,
            trygdetid = 5,
            poengaarTom1991 = 6,
            poengaarFom1992 = 7,
            grunnpensjon = 8,
            tilleggspensjon = 9,
            afpTillegg = 10,
            saertillegg = 11,
            afpGrad = 12,
            erAvkortet = true,
            fremtidigePensjonspoeng = 13.4
        )
    }
})