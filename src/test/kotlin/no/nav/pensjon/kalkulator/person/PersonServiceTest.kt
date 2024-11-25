package no.nav.pensjon.kalkulator.person

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PersonFactory
import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.uttaksalder.normalder.NormertPensjoneringsalderService
import no.nav.pensjon.kalkulator.uttaksalder.normalder.PensjoneringAldre
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
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
    private lateinit var normalderService: NormertPensjoneringsalderService

    @Mock
    private lateinit var navnRequirement: NavnRequirement

    @Test
    fun `getPerson returns person when valid foedselsnummer`() {
        val person = arrangePerson(pid = pid)
        service().getPerson() shouldBe person
    }

    @Test
    fun `getPerson returns person with normert pensjoneringsalder`() {
        arrangePerson(
            pensjoneringAldre = PensjoneringAldre(
                normalder = Alder(aar = 67, maaneder = 4),
                nedreAldresgrense = Alder(aar = 62, maaneder = 4)
            )
        )

        service().getPerson().pensjoneringAldre shouldBe PensjoneringAldre(
            normalder = Alder(aar = 67, maaneder = 4),
            nedreAldresgrense = Alder(aar = 62, maaneder = 4)
        )
    }

    @Test
    fun `getPerson uses cache`() {
        arrangePerson()

        with(service()) {
            getPerson() // causes person to be cached
            getPerson() // cache used
            verify(client, times(1)).fetchPerson(pid, fetchFulltNavn = false)
        }
    }

    @Test
    fun `getPerson throws NotFoundException when invalid foedselsnummer`() {
        arrangePerson(pid = Pid("bad"))
        val exception = assertThrows<NotFoundException> { service().getPerson() }
        assertEquals("person", exception.message)
    }

    private fun service() =
        PersonService(client, pidGetter, aldersgruppeFinder, navnRequirement, normalderService)

    private fun arrangePerson(
        pid: Pid = PersonFactory.pid,
        pensjoneringAldre: PensjoneringAldre = NormertPensjoneringsalderService.defaultAldre
    ): Person {
        with(person().withPensjoneringAldre(pensjoneringAldre)) {
            `when`(pidGetter.pid()).thenReturn(pid)
            `when`(client.fetchPerson(pid, fetchFulltNavn = false)).thenReturn(this)
            `when`(aldersgruppeFinder.aldersgruppe(person = this)).thenReturn("")
            `when`(navnRequirement.needFulltNavn()).thenReturn(false)
            `when`(normalderService.getAldre(this.foedselsdato)).thenReturn(pensjoneringAldre)
            return this
        }
    }
}
