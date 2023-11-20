package no.nav.pensjon.kalkulator.person

import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.TimeProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
class AldersgruppeFinderTest {

    @Mock
    private lateinit var timeProvider: TimeProvider

    @Test
    fun `aldersgruppe returns person's 10-year aldersgruppe`() {
        `when`(timeProvider.time()).thenReturn(LocalDateTime.of(2020, 1, 1, 12, 0, 0))

        val finder = AldersgruppeFinder(timeProvider)

        assertEquals("60-69", finder.aldersgruppe(person(1959)))
        assertEquals("60-69", finder.aldersgruppe(person(1960)))
        assertEquals("50-59", finder.aldersgruppe(person(1961)))
    }

    private companion object {
        private fun person(foedselsaar: Int) =
            Person(
                "",
                LocalDate.of(foedselsaar, 1, 1),
                Sivilstand.UOPPGITT,
                AdressebeskyttelseGradering.UGRADERT
            )
    }
}
