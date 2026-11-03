package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.tech.web.BadRequestException

class SimuleringV2GradertUttakSpecTest : ShouldSpec({

    context("toInternalValue") {
        context("OK verdier") {
            should("mappe til intern domenerepresentasjon") {
                SimuleringV2GradertUttakSpec(
                    grad = 50,
                    uttaksalder = SimuleringV2AlderSpec(aar = 62, maaneder = 4),
                    aarligInntektVsaPensjonBeloep = 0
                ).toInternalValue() shouldBe GradertUttak(
                    grad = Uttaksgrad.FEMTI_PROSENT,
                    uttakFomAlder = Alder(aar = 62, maaneder = 4),
                    aarligInntekt = 0
                )
            }
        }

        context("ugyldig verdi for uttaksgrad") {
            should("gi feilmelding") {
                shouldThrow<BadRequestException> {
                    SimuleringV2GradertUttakSpec(
                        grad = 25,
                        uttaksalder = SimuleringV2AlderSpec(aar = 65, maaneder = 0),
                        aarligInntektVsaPensjonBeloep = 0
                    ).toInternalValue()
                }.message shouldBe "Ugyldig grad for gradert uttak 25"
            }
        }
    }
})