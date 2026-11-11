package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.simulering.SimulertTidsbegrensetOffentligAfp

class SimuleringV2TidsbegrensetOffentligAfpTest : ShouldSpec({

    should("konvertere intern domenerepresentasjon til eksternt overførbart objektformat") {
        SimuleringV2TidsbegrensetOffentligAfp.transferable(
            source = SimulertTidsbegrensetOffentligAfp(
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
        ) shouldBe SimuleringV2TidsbegrensetOffentligAfp(
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