package no.nav.pensjon.kalkulator.general

import no.nav.pensjon.kalkulator.simulering.PensjonUtil
import java.time.LocalDate

/**
 * Helt uttak = uttak av full (100 %) alderpensjon.
 * Dette er en livsvarig ytelse (dermed ingen sluttdato for uttak).
 */
data class HeltUttak(
    val uttakFomAlder: Alder,
    val aarligInntekt: Int,
    val inntektTomAlder: Alder,
    val foedselDato: LocalDate
) {
    val uttakFomDato: LocalDate = PensjonUtil.uttakDato(foedselDato, uttakFomAlder)
}
