package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.AvvisningAarsak
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.TilgangResult

class TilgangsnektDtoTest : ShouldSpec({

    context("funksjonen 'transferable'") {
        should("mappe tilgangsresultat til overførbart format (DTO)") {
            TilgangsnektDto.transferable(
                source = TilgangResult(
                    innvilget = false,
                    avvisningAarsak = AvvisningAarsak.HABILITET,
                    begrunnelse = "inhabil",
                    traceId = "t1"
                )
            ) shouldBe TilgangsnektDto(
                type = AvvisningAarsakDto.HABILITET,
                detail = "inhabil",
                traceId = "t1"
            )
        }

        should("mappe feilmelding til overførbart format (DTO)") {
            TilgangsnektDto.transferable(
                feilmelding = "oops"
            ) shouldBe TilgangsnektDto(
                type = AvvisningAarsakDto.TILGANGSSJEKK_FEIL,
                detail = "oops",
                traceId = "00000000000000000000000000000000" // io.opentelemetry.api.trace.Span default
            )
        }
    }
})