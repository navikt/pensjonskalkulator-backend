package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.SimulatorAnonymAlderSpec
import java.time.LocalDate

/**
 * Helper class used to map from 'alder' to 'dato'.
 */
data class SimulatorAnonymAlderDato(
    val alder: SimulatorAnonymAlderSpec,
    val dato: LocalDate
) {
    constructor(foedselsdato: LocalDate, alder: SimulatorAnonymAlderSpec)
            : this(alder, datoVedAlder(foedselsdato, alder))

    private companion object {
        /**
         * Pensjonsrelatert dato er første dag i måneden etter 'aldersbasert' dato.
         */
        private fun datoVedAlder(foedselsdato: LocalDate, alder: SimulatorAnonymAlderSpec): LocalDate =
            foedselsdato
                .plusYears(alder.aar.toLong())
                .withDayOfMonth(1) // første dag i...
                .plusMonths(alder.maaneder.toLong() + 1L) // ...måneden etter 'aldersbasert' dato
    }
}
