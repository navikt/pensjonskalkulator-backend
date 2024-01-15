package no.nav.pensjon.kalkulator.general

import no.nav.pensjon.kalkulator.simulering.PensjonUtil
import java.time.LocalDate

/**
 * Gradert uttak = delvis uttak av alderpensjon (mindre enn 100 %)
 * Kun startalder ('uttakFomAlder') er nødvendig, siden sluttalder er gitt ved startalder for helt (100 %) uttak.
 */
data class GradertUttak(
    val grad: Uttaksgrad,
    val uttakFomAlder: Alder,
    val aarligInntekt: Int
)

data class UttaksalderGradertUttak(
    val grad: Uttaksgrad,
    val uttakFomAlder: Alder,
    val aarligInntekt: Int,
    val foedselDato: LocalDate
) {
    val uttakFomDato: LocalDate = PensjonUtil.uttakDato(foedselDato, uttakFomAlder)
}
