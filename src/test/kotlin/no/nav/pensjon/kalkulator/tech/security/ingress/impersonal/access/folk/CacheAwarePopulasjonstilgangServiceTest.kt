package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.AvvisningAarsak
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.TilgangResult
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor

class CacheAwarePopulasjonstilgangServiceTest : ShouldSpec({

    should("gi null når populasjonstilgang innvilget") {
        CacheAwarePopulasjonstilgangService(
            navIdExtractor = arrangeNavIdExtractor(),
            populasjonstilgangService = arrangePopulasjonstilgang(result = innvilget)
        ).eventuellTilgangsnektAarsak(pid) shouldBe null
    }

    should("gi årsak når populasjonstilgang avvist") {
        CacheAwarePopulasjonstilgangService(
            navIdExtractor = arrangeNavIdExtractor(),
            populasjonstilgangService = arrangePopulasjonstilgang(result = avvist)
        ).eventuellTilgangsnektAarsak(pid) shouldBe TilgangResult(
            innvilget = false,
            avvisningAarsak = AvvisningAarsak.GEOGRAFISK,
            begrunnelse = "årsaken"
        )
    }

    should("gi årsak når populasjonstilgangssjekk feiler") {
        CacheAwarePopulasjonstilgangService(
            navIdExtractor = arrangeNavIdExtractor(),
            populasjonstilgangService = arrangePopulasjonstilgangError()
        ).eventuellTilgangsnektAarsak(pid) shouldBe TilgangResult(
            innvilget = false,
            avvisningAarsak = AvvisningAarsak.POPULASJONSTILGANGSSJEKK_FEIL,
            begrunnelse = "feil"
        )
    }

    should("gi årsak når uthenting av Nav-ID feiler") {
        CacheAwarePopulasjonstilgangService(
            navIdExtractor = arrangeNavIdError(),
            populasjonstilgangService = arrangePopulasjonstilgang(innvilget)
        ).eventuellTilgangsnektAarsak(pid) shouldBe TilgangResult(
            innvilget = false,
            avvisningAarsak = AvvisningAarsak.POPULASJONSTILGANGSSJEKK_FEIL,
            begrunnelse = "defekt"
        )
    }
})

private val innvilget = TilgangResult(innvilget = true)

private val avvist =
    TilgangResult(
        innvilget = false,
        avvisningAarsak = AvvisningAarsak.GEOGRAFISK,
        begrunnelse = "årsaken"
    )

private val feil =
    TilgangResult(
        innvilget = false,
        avvisningAarsak = AvvisningAarsak.POPULASJONSTILGANGSSJEKK_FEIL,
        begrunnelse = "feil"
    )

private fun arrangeNavIdExtractor(): SecurityContextNavIdExtractor =
    mockk { every { id() } returns "Z123456" }

private fun arrangeNavIdError(): SecurityContextNavIdExtractor =
    mockk { every { id() } throws RuntimeException("defekt") }

private fun arrangePopulasjonstilgang(result: TilgangResult): PopulasjonstilgangService =
    mockk { every { sjekkTilgang(pid) } returns result }

private fun arrangePopulasjonstilgangError(): PopulasjonstilgangService =
    mockk { every { sjekkTilgang(pid) } returns feil }