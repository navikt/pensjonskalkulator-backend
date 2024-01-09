package no.nav.pensjon.kalkulator.general

import no.nav.pensjon.kalkulator.simulering.PensjonUtil
import java.time.LocalDate

data class GradertUttak(
    val grad: Uttaksgrad,
    val heltUttakAlder: Alder,
    val inntektUnderGradertUttak: Int,
    val foedselDato: LocalDate
) {
    val heltUttakDato: LocalDate = PensjonUtil.uttakDato(foedselDato, heltUttakAlder)
}
