package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.Alternativ
import no.nav.pensjon.kalkulator.simulering.Vilkaarsproeving

class SimuleringV2VilkaarsproevingsresultatTest : ShouldSpec({

    context("funksjonen 'transferable'") {
        context("innvilget") {
            should("mappe hvorvidt innvilget og AFPs betydning for innvilgelse") {
                SimuleringV2Vilkaarsproevingsresultat.transferable(
                    Vilkaarsproeving(
                        innvilget = true,
                        alternativ = null,
                        livsvarigOffentligAfpHarBetydningForInnvilgelse = true
                    )
                ) shouldBe SimuleringV2Vilkaarsproevingsresultat(
                    erInnvilget = true,
                    alternativ = null,
                    livsvarigOffentligAfpHarBetydningForInnvilgelse = true
                )
            }
        }

        context("avslag med alternativ") {
            should("mappe hvorvidt innvilget og alternativet") {
                SimuleringV2Vilkaarsproevingsresultat.transferable(
                    Vilkaarsproeving(
                        innvilget = false,
                        alternativ = Alternativ(
                            gradertUttakAlder = Alder(aar = 63, maaneder = 2),
                            uttakGrad = Uttaksgrad.AATTI_PROSENT,
                            heltUttakAlder = Alder(aar = 66, maaneder = 5)
                        ),
                        livsvarigOffentligAfpHarBetydningForInnvilgelse = null
                    )
                ) shouldBe SimuleringV2Vilkaarsproevingsresultat(
                    erInnvilget = false,
                    alternativ = SimuleringV2Uttaksparametre(
                        gradertUttakAlder = SimuleringV2Alder(aar = 63, maaneder = 2),
                        uttaksgrad = 80,
                        heltUttakAlder = SimuleringV2Alder(aar = 66, maaneder = 5)
                    ),
                    livsvarigOffentligAfpHarBetydningForInnvilgelse = null
                )
            }
        }
    }
})