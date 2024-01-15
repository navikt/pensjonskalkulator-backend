package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.person.Sivilstand

/**
 * Specifies impersonal parameters for simulering.
 * 'Impersonal' means parameters that do not require person ID to be known.
 */
data class ImpersonalSimuleringSpec(
    val simuleringType: SimuleringType,
    val sivilstand: Sivilstand? = null,
    val epsHarInntektOver2G: Boolean,
    val forventetAarligInntektFoerUttak: Int? = null,
    val gradertUttak: GradertUttak? = null,
    val heltUttak: HeltUttak
)
