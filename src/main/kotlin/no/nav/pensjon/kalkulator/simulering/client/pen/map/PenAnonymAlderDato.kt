package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymAlderSpec
import java.time.LocalDate

/**
 * Helper class used to map from 'alder' to 'dato'.
 */
data class PenAnonymAlderDato(
    val alder: PenAnonymAlderSpec,
    val dato: LocalDate
) {
    constructor(foedselsdato: LocalDate, alder: PenAnonymAlderSpec)
            : this(alder, datoVedAlder(foedselsdato, alder))

    private companion object {
        /**
         * Pensjonsrelatert dato er første dag i måneden etter 'aldersbasert' dato.
         */
        private fun datoVedAlder(foedselsdato: LocalDate, alder: PenAnonymAlderSpec): LocalDate =
            foedselsdato
                .plusYears(alder.aar.toLong())
                .withDayOfMonth(1) // første dag i...
                .plusMonths(alder.maaneder.toLong() + 1L) // ...måneden etter 'aldersbasert' dato
    }
}
