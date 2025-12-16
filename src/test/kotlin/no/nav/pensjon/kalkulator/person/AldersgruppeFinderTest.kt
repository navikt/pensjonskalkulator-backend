package no.nav.pensjon.kalkulator.person

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.TimeProvider
import java.time.LocalDate
import java.time.LocalDateTime

class AldersgruppeFinderTest : ShouldSpec({

    should("return person's 10-year aldersgruppe") {
        val timeProvider = mockk<TimeProvider>().apply {
            every { time() } returns LocalDateTime.of(2020, 1, 1, 12, 0, 0)
        }

        val finder = AldersgruppeFinder(timeProvider)

        finder.aldersgruppe(person(foedselsaar = 1918)) shouldBe "100-109"
        finder.aldersgruppe(person(foedselsaar = 1959)) shouldBe "60-69"
        finder.aldersgruppe(person(foedselsaar = 1960)) shouldBe "60-69"
        finder.aldersgruppe(person(foedselsaar = 1961)) shouldBe "50-59"
        finder.aldersgruppe(person(foedselsaar = 2010)) shouldBe "10-19"
    }
})

private fun person(foedselsaar: Int) =
    Person(
        navn = "",
        fornavn = "",
        foedselsdato = LocalDate.of(foedselsaar, 1, 1)
    )
