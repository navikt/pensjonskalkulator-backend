package no.nav.pensjon.kalkulator.person

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.PersonFactory.skiltPerson
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class PersonServiceTest {

    @Mock
    private lateinit var client: PersonClient

    @Mock
    private lateinit var pidGetter: PidGetter

    @Mock
    private lateinit var aldersgruppeFinder: AldersgruppeFinder

    @Test
    fun getPerson() {
        val skiltPerson = skiltPerson()
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(client.fetchPerson(pid)).thenReturn(skiltPerson)
        `when`(aldersgruppeFinder.aldersgruppe(skiltPerson)).thenReturn("")

        val person = PersonService(client, pidGetter, aldersgruppeFinder).getPerson()

        person shouldBe skiltPerson
    }
}
