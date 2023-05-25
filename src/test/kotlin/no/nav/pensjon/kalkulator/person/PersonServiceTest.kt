package no.nav.pensjon.kalkulator.person

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

        assertEquals(Sivilstand.SKILT, person.sivilstand)
    }

    private companion object {

        private const val FNR = "12906498357"
        private val pid = Pid(FNR)

        private fun skiltPerson() = Person(LocalDate.of(1964, 1, 1), Land.NORGE, Sivilstand.SKILT)
    }
}
