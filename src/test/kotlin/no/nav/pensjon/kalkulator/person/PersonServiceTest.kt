package no.nav.pensjon.kalkulator.person

import io.kotest.assertions.throwables.shouldThrow
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
import java.time.LocalDate

class PersonServiceTest : ShouldSpec({

    should("return person when valid fødselsnummer") {
        PersonService(
            client = arrangePerson(),
            pidGetter = arrangePid(),
            aldersgruppeFinder = arrangeAldersgruppe(),
            navnRequirement,
            normalderService = arrangeNormalder()
        ).getPerson() shouldBe person().withPensjoneringAldre(pensjoneringAldre = defaultAldersgrenser)
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

    context("invalid fødselsnummer") {
        should("throw 'not found' exception") {
            shouldThrow<NotFoundException> {
                PersonService(
                    client = arrangePerson(),
                    pidGetter = arrangePid(pid = Pid("bad")),
                    aldersgruppeFinder = arrangeAldersgruppe(),
                    navnRequirement,
                    normalderService = arrangeNormalder()
                ).getPerson() shouldBe person().withPensjoneringAldre(pensjoneringAldre = defaultAldersgrenser)
            }.message shouldBe "person"
        }
    }

    context("missing fødselsdato") {
        should("throw 'not found' exception") {
            shouldThrow<NotFoundException> {
                PersonService(
                    client = arrangePerson(foedselsdato = datoSomRepresentererManglendeFoedselsdato),
                    pidGetter = arrangePid(),
                    aldersgruppeFinder = arrangeAldersgruppe(),
                    navnRequirement,
                    normalderService = arrangeNormalder()
                ).getPerson() shouldBe person().withPensjoneringAldre(pensjoneringAldre = defaultAldersgrenser)
            }.message shouldBe "fødselsdato"
        }
    }
})

/**
 * Datoer tidligere enn Person.minimumFoedselsdato representerer manglende fødselsdato.
 */
private val datoSomRepresentererManglendeFoedselsdato = LocalDate.of(1900, 12, 31)

private val navnRequirement = mockk<NavnRequirement>(relaxed = true)

private fun arrangePid(pid: Pid = PersonFactory.pid): PidGetter =
    mockk { every { pid() } returns pid }

private fun arrangeNormalder(pensjonsaldre: Aldersgrenser = defaultAldersgrenser): NormertPensjonsalderService =
    mockk { every { aldersgrenser(any<LocalDate>()) } returns pensjonsaldre }

private fun arrangePerson(
    foedselsdato: LocalDate = LocalDate.of(1963, 12, 31),
    pensjoneringAldre: Aldersgrenser = defaultAldersgrenser
): PersonClient =
    mockk {
        every {
            fetchPerson(any(), any())
        } returns person(foedselsdato = foedselsdato).withPensjoneringAldre(pensjoneringAldre)
    }

private fun arrangeAldersgruppe(): AldersgruppeFinder =
    mockk { every { aldersgruppe(any()) } returns "" }