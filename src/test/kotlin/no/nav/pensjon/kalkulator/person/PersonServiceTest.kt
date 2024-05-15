package no.nav.pensjon.kalkulator.person

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.PersonFactory.skiltPerson
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    @Mock
    private lateinit var navnRequirement: NavnRequirement

    @Test
    fun `getPerson returns person when valid foedselsnummer`() {
        val skiltPerson = skiltPerson()
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(client.fetchPerson(pid, fetchFulltNavn = false)).thenReturn(skiltPerson)
        `when`(aldersgruppeFinder.aldersgruppe(skiltPerson)).thenReturn("")
        `when`(navnRequirement.needFulltNavn()).thenReturn(false)

        val person = PersonService(client, pidGetter, aldersgruppeFinder, navnRequirement).getPerson()

        person shouldBe skiltPerson
    }

    @Test
    fun `getPerson throws NotFoundException when invalid foedselsnummer`() {
        val skiltPerson = skiltPerson()
        `when`(pidGetter.pid()).thenReturn(Pid("bad"))
        `when`(client.fetchPerson(pid, fetchFulltNavn = false)).thenReturn(skiltPerson)

        val exception = assertThrows<NotFoundException> {
            PersonService(client, pidGetter, aldersgruppeFinder, navnRequirement).getPerson()
        }

        assertEquals("person", exception.message)
    }
}
