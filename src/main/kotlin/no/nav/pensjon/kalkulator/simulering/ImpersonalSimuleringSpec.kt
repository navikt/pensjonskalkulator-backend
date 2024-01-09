package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.person.Sivilstand
import java.time.LocalDate

/**
 * Specifies impersonal parameters for simulering.
 * 'Impersonal' means parameters that do not require person ID to be known.
 */
data class ImpersonalSimuleringSpec(
    val simuleringType: SimuleringType,
    val foersteUttakAlder: Alder,
    val foedselDato: LocalDate,
    val epsHarInntektOver2G: Boolean,
    val forventetInntekt: Int? = null,
    val sivilstand: Sivilstand? = null,
    val gradertUttak: GradertUttak? = null
) {
    val foersteUttakDato: LocalDate = PensjonUtil.uttakDato(foedselDato, foersteUttakAlder)
}
