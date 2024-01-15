package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.general.Alder
import java.time.LocalDate

object PensjonUtil {

    /**
     * Uttaksdato = første dag i måneden etter uttaksalder.
     */
    fun uttakDato(foedselDato: LocalDate, uttakAlder: Alder) =
        LocalDate.of(foedselDato.year, foedselDato.month, 1)
            .plusYears(uttakAlder.aar.toLong())
            .plusMonths(uttakAlder.maaneder.toLong() + 1)
}
