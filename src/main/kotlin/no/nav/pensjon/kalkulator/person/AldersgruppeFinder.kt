package no.nav.pensjon.kalkulator.person

import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.TimeProvider
import org.springframework.stereotype.Component

/**
 * Finds the aldersgruppe a person belongs to, e.g. "60-69".
 */
@Component
class AldersgruppeFinder(val timeProvider: TimeProvider) {

    fun aldersgruppe(person: Person): String =
        aldersgruppe(timeProvider.time().year - person.foedselsdato.year)

    private fun aldersgruppe(alder: Int): String = (alder / 10).let { "${it}0-${it}9" }
}
