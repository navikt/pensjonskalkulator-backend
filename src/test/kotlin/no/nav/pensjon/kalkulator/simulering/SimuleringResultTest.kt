package no.nav.pensjon.kalkulator.simulering

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class SimuleringResultTest : ShouldSpec({

    context("withAlderAar") {
        should("tilordne alder (antall år) til en kopi av objektet") {
            SimuleringResult(
                alderspensjonListe = emptyList(),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 1, heltUttak = 2),
                livsvarigOffentligAfpListe = emptyList(),
                tidsbegrensetOffentligAfp = null,
                privatAfpListe = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(innvilget = false, alternativ = null),
                harForLiteTrygdetid = true,
                trygdetid = 3,
                opptjeningListe = listOf(
                    SimulertOpptjening(
                        aarstall = 2021,
                        pensjonsgivendeInntektBeloep = 10000,
                        pensjonspoeng = 1.2,
                        pensjonsbeholdningBeloep = 333000
                    )
                ),
                alderAar = null,
                problem = null
            ).withAlderAar(65) shouldBe SimuleringResult(
                alderspensjonListe = emptyList(),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 1, heltUttak = 2),
                livsvarigOffentligAfpListe = emptyList(),
                tidsbegrensetOffentligAfp = null,
                privatAfpListe = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(innvilget = false, alternativ = null),
                harForLiteTrygdetid = true,
                trygdetid = 3,
                opptjeningListe = listOf(
                    SimulertOpptjening(
                        aarstall = 2021,
                        pensjonsgivendeInntektBeloep = 10000,
                        pensjonspoeng = 1.2,
                        pensjonsbeholdningBeloep = 333000
                    )
                ),
                alderAar = 65,
                problem = null
            )
        }
    }
})
