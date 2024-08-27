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
    constructor(fodselsdato: LocalDate, alder: PenAnonymAlderSpec)
            : this(alder, datoVedAlder(fodselsdato, alder))

    private companion object {
        private fun datoVedAlder(fodselsdato: LocalDate, alder: PenAnonymAlderSpec): LocalDate =
            alder.let {
                fodselsdato
                    .plusYears(it.aar.toLong())
                    .plusMonths(it.maaneder.toLong())
            }
    }
}
