package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import java.time.LocalDate

class FortroligAdresseServiceTest : ShouldSpec({

    should("return person's adressebeskyttelsesgradering") {
        val personClient = mockk<PersonClient>().apply {
            every {
                fetchAdressebeskyttelse(any())
            } returns Person(
                navn = "F E",
                fornavn = "F",
                foedselsdato = LocalDate.MIN,
                sivilstand = Sivilstand.UOPPGITT,
                adressebeskyttelse = AdressebeskyttelseGradering.STRENGT_FORTROLIG
            )
        }

        FortroligAdresseService(personClient).adressebeskyttelseGradering(pid) shouldBe
                AdressebeskyttelseGradering.STRENGT_FORTROLIG
    }

    should("return 'unknown' by default") {
        val personClient = mockk<PersonClient>().apply {
            every { fetchAdressebeskyttelse(any()) } returns null
        }

        FortroligAdresseService(personClient).adressebeskyttelseGradering(pid) shouldBe
                AdressebeskyttelseGradering.UNKNOWN
    }
})
