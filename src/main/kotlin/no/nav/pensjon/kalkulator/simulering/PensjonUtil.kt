package no.nav.pensjon.kalkulator.simulering

import java.time.LocalDate


object PensjonUtil {

    /**
     * Første uttaksdato = første dag i måneden etter at pensjoneringsalder er nådd.
     */
    fun foersteUttaksdato(foedselsdato: LocalDate, pensjoneringsalder: Int) =
        LocalDate.of(foedselsdato.year + pensjoneringsalder, foedselsdato.month, 1)
            .plusMonths(1)

    fun pensjoneringsaar(foedselsdato: LocalDate, pensjoneringsalder: Int) =
        foersteUttaksdato(foedselsdato, pensjoneringsalder).year
}
