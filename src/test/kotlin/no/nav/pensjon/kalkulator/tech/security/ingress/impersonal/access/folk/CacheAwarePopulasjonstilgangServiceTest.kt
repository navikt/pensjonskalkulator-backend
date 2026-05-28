package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor

class CacheAwarePopulasjonstilgangServiceTest : ShouldSpec({

    should("gi null når populasjonstilgang innvilget") {
        CacheAwarePopulasjonstilgangService(
            navIdExtractor = arrangeNavIdExtractor(),
            populasjonstilgangService = arrangePopulasjonstilgang(result = innvilget())
        ).eventuellTilgangsnektAarsak(pid) shouldBe null
    }

    should("gi årsak når populasjonstilgang avvist") {
        CacheAwarePopulasjonstilgangService(
            navIdExtractor = arrangeNavIdExtractor(),
            populasjonstilgangService = arrangePopulasjonstilgang(result = avvist())
        ).eventuellTilgangsnektAarsak(pid) shouldBe "GEOGRAFISK: årsaken"
    }

    should("gi årsak når populasjonstilgangssjekk feiler") {
        CacheAwarePopulasjonstilgangService(
            navIdExtractor = arrangeNavIdExtractor(),
            populasjonstilgangService = arrangePopulasjonstilgangError()
        ).eventuellTilgangsnektAarsak(pid) shouldBe "POPULASJONSTILGANGSSJEKK_FEILET: feil"
    }

    should("gi årsak når uthenting av Nav-ID feiler") {
        CacheAwarePopulasjonstilgangService(
            navIdExtractor = arrangeNavIdError(),
            populasjonstilgangService = arrangePopulasjonstilgang(innvilget())
        ).eventuellTilgangsnektAarsak(pid) shouldBe "POPULASJONSTILGANGSSJEKK_FEILET: defekt"
    }
})

private fun arrangeNavIdExtractor(): SecurityContextNavIdExtractor =
    mockk<SecurityContextNavIdExtractor>().apply {
        every { id() } returns "Z123456"
    }

private fun arrangeNavIdError(): SecurityContextNavIdExtractor =
    mockk<SecurityContextNavIdExtractor>().apply {
        every { id() } throws RuntimeException("defekt")
    }

private fun arrangePopulasjonstilgang(result: TilgangResult): PopulasjonstilgangService =
    mockk<PopulasjonstilgangService>().apply {
        every { sjekkTilgang(pid) } returns result
    }

private fun arrangePopulasjonstilgangError(): PopulasjonstilgangService =
    mockk<PopulasjonstilgangService>().apply {
        every { sjekkTilgang(pid) } returns feil()
    }

private fun innvilget() =
    TilgangResult(
        innvilget = true,
        avvisningAarsak = null,
        begrunnelse = null,
        traceId = null
    )

private fun avvist() =
    TilgangResult(
        innvilget = false,
        avvisningAarsak = AvvisningAarsak.GEOGRAFISK,
        begrunnelse = "årsaken",
        traceId = null
    )

private fun feil() =
    TilgangResult(
        innvilget = false,
        avvisningAarsak = AvvisningAarsak.POPULASJONSTILGANGSSJEKK_FEILET,
        begrunnelse = "feil",
        traceId = null
    )