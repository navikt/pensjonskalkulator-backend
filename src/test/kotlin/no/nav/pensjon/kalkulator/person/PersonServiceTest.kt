package no.nav.pensjon.kalkulator.person

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
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
class PersonServiceTest {

    @Mock
    private lateinit var client: PersonClient

    @Mock
    private lateinit var pidGetter: PidGetter

    @Test
    fun getPerson() {
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(client.getPerson(pid)).thenReturn(skiltPerson())

        val person = PersonService(client, pidGetter).getPerson()

        assertEquals("Fornavn1", person.fornavn)
        assertEquals(LocalDate.of(1964, 10, 12), person.foedselsdato)
        assertEquals(Sivilstand.SKILT, person.sivilstand)
    }
}
