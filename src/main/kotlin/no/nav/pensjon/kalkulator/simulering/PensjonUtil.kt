package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.general.Alder
import java.time.LocalDate


object PensjonUtil {

    /**
     * Første uttaksdato = første dag i måneden etter første uttaksalder.
     */
    fun foersteUttaksdato(foedselsdato: LocalDate, foersteUttaksalder: Alder) =
        LocalDate.of(foedselsdato.year, foedselsdato.month, 1)
            .plusYears(foersteUttaksalder.aar.toLong())
            .plusMonths(foersteUttaksalder.maaneder.toLong() + 1)
}
