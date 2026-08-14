package no.nav.pensjon.kalkulator.person.relasjon.eps

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon
import no.nav.pensjon.kalkulator.person.relasjon.Relasjonstype
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.AvvisningAarsak
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.TilgangResult
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.CacheAwarePopulasjonstilgangService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.PopulasjonstilgangNektetException
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.Populasjonstilgangsnekt
import java.time.LocalDate

class EpsServiceTest : ShouldSpec({

    context("nyligsteRelasjon - suksess") {
        should("gi familierelasjon") {
            val familierelasjon = Familierelasjon(
                pid,
                fom = LocalDate.of(2021, 1, 1),
                relasjonstype = Relasjonstype.SAMBOER,
                relasjonPersondata = null
            )

            EpsService(
                client = mockk { every { fetchNyligsteEps(any()) } returns familierelasjon },
                personService = mockk(),
                pidGetter = mockk(relaxed = true),
                populasjonstilgangService = arrangeTilgang(tilgangsnektBegrunnelse = null)
            ).nyligsteRelasjon(Sivilstatus.SAMBOER) shouldBe familierelasjon
        }
    }

    context("nyligsteRelasjon - tilgang nektet") {
        should("kaste 'access denied' exception med beskrivelse av årsak") {
            shouldThrow<PopulasjonstilgangNektetException> {
                EpsService(
                    client = mockk(relaxed = true),
                    personService = mockk(),
                    pidGetter = mockk(relaxed = true),
                    populasjonstilgangService = arrangeTilgang(tilgangsnektBegrunnelse = "egen ansatt")
                ).nyligsteRelasjon(Sivilstatus.GIFT)
            } shouldBe PopulasjonstilgangNektetException(
                message = "Tilgang til EPS nektet",
                aarsak = Populasjonstilgangsnekt(aarsak = AvvisningAarsak.SKJERMING, begrunnelse = "egen ansatt")
            )
        }
    }
})

private fun arrangeTilgang(tilgangsnektBegrunnelse: String?): CacheAwarePopulasjonstilgangService =
    mockk {
        every {
            eventuellTilgangsnektAarsak(any(), any())
        } returns tilgangsnektBegrunnelse?.let {
            TilgangResult(
                innvilget = false,
                avvisningAarsak = AvvisningAarsak.SKJERMING,
                begrunnelse = it
            )
        }
    }