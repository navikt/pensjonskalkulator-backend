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
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.CacheAwarePopulasjonstilgangService
import org.springframework.security.access.AccessDeniedException
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
                client = mockk {
                    every {
                        fetchNyligsteEps(any(), any(), any())
                    } returns familierelasjon
                },
                personService = mockk(),
                pidGetter = mockk(relaxed = true),
                populasjonstilgangService = arrangeTilgang(tilgangsnektAarsak = null)
            ).nyligsteRelasjon(Sivilstatus.SAMBOER) shouldBe familierelasjon
        }
    }

    context("nyligsteRelasjon - tilgang nektet") {
        should("throw 'access denied' exception") {
            shouldThrow<AccessDeniedException> {
                EpsService(
                    client = mockk(relaxed = true),
                    personService = mockk(),
                    pidGetter = mockk(relaxed = true),
                    populasjonstilgangService = arrangeTilgang(tilgangsnektAarsak = "egen ansatt")
                ).nyligsteRelasjon(Sivilstatus.GIFT)
            }.message shouldBe "Tilgang til EPS nektet: egen ansatt"
        }
    }
})

private fun arrangeTilgang(tilgangsnektAarsak: String?): CacheAwarePopulasjonstilgangService =
    mockk { every { eventuellTilgangsnektAarsak(any()) } returns tilgangsnektAarsak }
