package no.nav.pensjon.kalkulator.simulering

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class SimuleringResultTest : ShouldSpec({

    context("withAlderAar") {
        should("tilordne alder (antall år) til en kopi av objektet") {
            SimuleringResult(
                alderspensjon = emptyList(),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(
                    gradertUttak = 1,
                    heltUttak = 2
                ),
                pre2025OffentligAfp = null,
                afpPrivat = emptyList(),
                afpOffentlig = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(
                    innvilget = false,
                    alternativ = null
                ),
                harForLiteTrygdetid = true,
                trygdetid = 3,
                opptjeningGrunnlagListe = listOf(
                    SimulertOpptjeningGrunnlag(
                        aar = 2021,
                        pensjonsgivendeInntektBeloep = 10000
                    )
                ),
                alderAar = null,
                problem = null
            ).withAlderAar(65) shouldBe SimuleringResult(
                alderspensjon = emptyList(),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(
                    gradertUttak = 1,
                    heltUttak = 2
                ),
                pre2025OffentligAfp = null,
                afpPrivat = emptyList(),
                afpOffentlig = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(
                    innvilget = false,
                    alternativ = null
                ),
                harForLiteTrygdetid = true,
                trygdetid = 3,
                opptjeningGrunnlagListe = listOf(
                    SimulertOpptjeningGrunnlag(
                        aar = 2021,
                        pensjonsgivendeInntektBeloep = 10000
                    )
                ),
                alderAar = 65,
                problem = null
            )
        }
    }
})
