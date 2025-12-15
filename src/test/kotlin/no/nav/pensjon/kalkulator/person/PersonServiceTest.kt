package no.nav.pensjon.kalkulator.person

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
import org.junit.jupiter.api.assertThrows

class PersonServiceTest : ShouldSpec({

    should("return person when valid fødselsnummer") {
        PersonService(
            client = arrangePerson(),
            pidGetter = arrangePid(),
            aldersgruppeFinder = arrangeAldersgruppe(),
            navnRequirement,
            normalderService = arrangeNormalder()
        ).getPerson() shouldBe person().withPensjoneringAldre(defaultAldersgrenser)
    }

    should("return person with normert pensjonsalder") {
        PersonService(
            client = arrangePerson(),
            pidGetter = arrangePid(),
            aldersgruppeFinder = arrangeAldersgruppe(),
            navnRequirement,
            normalderService = arrangeNormalder(
                pensjonsaldre = Aldersgrenser(
                    aarskull = 1964,
                    nedreAlder = Alder(aar = 62, maaneder = 4),
                    normalder = Alder(aar = 67, maaneder = 4),
                    oevreAlder = Alder(aar = 75, maaneder = 4),
                    verdiStatus = VerdiStatus.PROGNOSE
                )
            )
        ).getPerson().pensjoneringAldre shouldBe
                Aldersgrenser(
                    aarskull = 1964,
                    nedreAlder = Alder(aar = 62, maaneder = 4),
                    normalder = Alder(aar = 67, maaneder = 4),
                    oevreAlder = Alder(aar = 75, maaneder = 4),
                    verdiStatus = VerdiStatus.PROGNOSE
                )
    }

    should("use cache") {
        val client = arrangePerson()
        with(
            PersonService(
                client,
                pidGetter = arrangePid(),
                aldersgruppeFinder = arrangeAldersgruppe(),
                navnRequirement,
                normalderService = arrangeNormalder()
            )
        ) {
            getPerson() // causes person to be cached
            getPerson() // cache used
            verify(exactly = 1) { client.fetchPerson(pid, fetchFulltNavn = false) }
        }
    }

    should("throw NotFoundException when invalid fødselsnummer") {
        assertThrows<NotFoundException> {
            PersonService(
                client = arrangePerson(),
                pidGetter = arrangePid(pid = Pid("bad")),
                aldersgruppeFinder = arrangeAldersgruppe(),
                navnRequirement,
                normalderService = arrangeNormalder()
            ).getPerson() shouldBe person().withPensjoneringAldre(defaultAldersgrenser)
        }.message shouldBe "person"
    }
})

private val navnRequirement = mockk<NavnRequirement>(relaxed = true)

private fun arrangePid(pid: Pid = PersonFactory.pid): PidGetter =
    mockk<PidGetter>().apply {
        every { pid() } returns pid
    }

private fun arrangeNormalder(pensjonsaldre: Aldersgrenser = defaultAldersgrenser): NormertPensjonsalderService =
    mockk<NormertPensjonsalderService>().apply {
        every { aldersgrenser(any()) } returns pensjonsaldre
    }

private fun arrangePerson(
    pensjoneringAldre: Aldersgrenser = defaultAldersgrenser
): PersonClient =
    mockk<PersonClient>().apply {
        every {
            fetchPerson(any(), any())
        } returns person().withPensjoneringAldre(pensjoneringAldre)
    }

private fun arrangeAldersgruppe(): AldersgruppeFinder =
    mockk<AldersgruppeFinder>().apply {
        every { aldersgruppe(any()) } returns ""
    }
