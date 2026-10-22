package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class SimuleringResultAdjusterTest : ShouldSpec({

    context("justerAlderspensjonListe") {
        context("alder 0 år er i listen, nåværende alder er ikke i listen") {
            should("erstatte alder 0 med nåværende alder") {
                val result = SimuleringResultAdjuster.justerAlderspensjonListe(
                    pensjonListe = listOf(
                        SimuleringV2Alderspensjon(
                            alderAar = 0,
                            beloep = 3,
                            inntektspensjonBeloep = 1,
                            basispensjonBeloep = 2,
                            garantipensjonBeloep = null,
                            garantipensjonSats = null,
                            garantitilleggBeloep = null,
                            restpensjonBeloep = null,
                            gjenlevendetillegg = null,
                            minstePensjonsnivaaSats = null,
                            delingstall = null,
                            forholdstall = null,
                            pensjonsbeholdningFoerUttakBeloep = null,
                            kapittel19Andel = null,
                            kapittel20Andel = null,
                            sluttpoengtall = null,
                            kapittel19Trygdetid = null,
                            kapittel20Trygdetid = null,
                            poengaarTom1991 = null,
                            poengaarFom1992 = null
                        )
                    ),
                    naavaerendeAlderAar = 61
                )

                result shouldHaveSize 1
                with(result.first()) {
                    alderAar shouldBe 61
                    beloep shouldBe 3
                    inntektspensjonBeloep shouldBe 1
                    basispensjonBeloep shouldBe 2
                }
            }
        }
    }
})