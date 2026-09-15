package no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon.dto

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.representasjon.Personalia
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon

class PensjonRepresentasjonResultTest : ShouldSpec({

    context("toInternalValue") {
        should("convert from external representation (data transfer object) to internal domain representation") {
            PensjonRepresentasjonResult(
                hasValidRepresentasjonsforhold = true,
                representertNavn = "NN",
                representertPid = pid.value
            ).toInternalValue() shouldBe Representasjon(
                isValid = true,
                fullmaktsgiver = Personalia(navn = "NN", pid = pid)
            )
        }
    }
})