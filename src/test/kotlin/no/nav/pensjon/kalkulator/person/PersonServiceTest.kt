package no.nav.pensjon.kalkulator.person

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PersonFactory
import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService.Companion.defaultAldersgrenser
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
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
    private lateinit var normalderService: NormertPensjonsalderService

    @Mock
    private lateinit var navnRequirement: NavnRequirement

    @Test
    fun `getPerson returns person when valid foedselsnummer`() {
        val person = arrangePerson(pid = pid)
        service().getPerson() shouldBe person
    }

    @Test
    fun `getPerson returns person with normert pensjonsalder`() {
        arrangePerson(
            pensjoneringAldre = Aldersgrenser(
                aarskull = 1964,
                nedreAlder = Alder(aar = 62, maaneder = 4),
                normalder = Alder(aar = 67, maaneder = 4),
                oevreAlder = Alder(aar = 75, maaneder = 4),
                verdiStatus = VerdiStatus.PROGNOSE
            )
        )

        service().getPerson().pensjoneringAldre shouldBe Aldersgrenser(
            aarskull = 1964,
            nedreAlder = Alder(aar = 62, maaneder = 4),
            normalder = Alder(aar = 67, maaneder = 4),
            oevreAlder = Alder(aar = 75, maaneder = 4),
            verdiStatus = VerdiStatus.PROGNOSE
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
        pensjoneringAldre: Aldersgrenser = defaultAldersgrenser
    ): Person {
        with(person().withPensjoneringAldre(pensjoneringAldre)) {
            `when`(pidGetter.pid()).thenReturn(pid)
            `when`(client.fetchPerson(pid, fetchFulltNavn = false)).thenReturn(this)
            `when`(aldersgruppeFinder.aldersgruppe(person = this)).thenReturn("")
            `when`(navnRequirement.needFulltNavn()).thenReturn(false)
            `when`(normalderService.aldersgrenser(this.foedselsdato)).thenReturn(pensjoneringAldre)
            return this
        }
    }
}
