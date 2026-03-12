package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.client.PopulasjonstilgangClient

class PopulasjonstilgangServiceTest : ShouldSpec({

    should("gi 'tilgang innvilget' når klienten gir 'tilgang innvilget'") {
        val result = TilgangResult(
            innvilget = true,
            avvisningAarsak = null,
            begrunnelse = null,
            traceId = null
        )

        PopulasjonstilgangService(
            client = mockk<PopulasjonstilgangClient>().apply {
                every { sjekkTilgang(any()) } returns result
            }
        ).sjekkTilgang(pid) shouldBe result
    }

    should("gi 'tilgang avvist' når klienten gir 'tilgang avvist'") {
        val result = TilgangResult(
            innvilget = false,
            avvisningAarsak = AvvisningAarsak.PERSON_UTLAND,
            begrunnelse = "b",
            traceId = "t"
        )

        PopulasjonstilgangService(
            client = mockk<PopulasjonstilgangClient>().apply {
                every { sjekkTilgang(any()) } returns result
            }
        ).sjekkTilgang(pid) shouldBe result
    }

    should("gi 'tilgang avvist' når klienten feiler")
    PopulasjonstilgangService(
        client = mockk<PopulasjonstilgangClient>().apply {
            every { sjekkTilgang(any()) } throws IllegalStateException("feil")
        }
    ).sjekkTilgang(pid) shouldBe TilgangResult(
        innvilget = false,
        avvisningAarsak = AvvisningAarsak.POPULASJONSTILGANGSSJEKK_FEILET,
        begrunnelse = "feil",
        traceId = null
    )
})
