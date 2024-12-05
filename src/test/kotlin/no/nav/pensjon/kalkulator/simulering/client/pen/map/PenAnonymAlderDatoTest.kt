package no.nav.pensjon.kalkulator.simulering.client.pen.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymAlderSpec
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PenAnonymAlderDatoTest {

    @Test
    fun `dato should be foerste dag i maaneden etter 'aldersbasert' dato`() {
        val alderDato = PenAnonymAlderDato(LocalDate.of(1963, 4, 5), PenAnonymAlderSpec(70, 0))
        alderDato.dato shouldBe LocalDate.of(2033, 5, 1)
    }
}
