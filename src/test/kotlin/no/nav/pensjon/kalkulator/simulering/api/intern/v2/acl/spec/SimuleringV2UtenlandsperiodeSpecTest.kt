package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.tech.web.BadRequestException
import java.time.LocalDate

class SimuleringV2UtenlandsperiodeSpecTest : ShouldSpec({

    context("toInternalValue") {
        context("OK verdier") {
            should("mappe til intern domenerepresentasjon") {
                SimuleringV2UtenlandsperiodeSpec(
                    fom = LocalDate.of(2021, 1, 1),
                    tom = LocalDate.of(2022, 12, 31),
                    landkode = "DZA",
                    arbeidetUtenlands = true
                ).toInternalValue() shouldBe Opphold(
                    fom = LocalDate.of(2021, 1, 1),
                    tom = LocalDate.of(2022, 12, 31),
                    land = Land.DZA,
                    arbeidet = true
                )
            }
        }

        context("ugyldig verdi for landkode") {
            should("gi feilmelding") {
                shouldThrow<BadRequestException> {
                    SimuleringV2UtenlandsperiodeSpec(
                        fom = LocalDate.of(2021, 1, 1),
                        landkode = "ÆØÅ",
                        arbeidetUtenlands = false
                    ).toInternalValue()
                }.message shouldBe "Ugyldig landkode for opphold ÆØÅ"
            }
        }
    }
})