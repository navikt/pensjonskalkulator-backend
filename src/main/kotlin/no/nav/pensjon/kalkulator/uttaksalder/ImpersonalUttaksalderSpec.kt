package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType

/**
 * Specifies impersonal parameters for finding første mulige uttaksalder.
 * 'Impersonal' means parameters that do not require person ID to be known.
 */
data class ImpersonalUttaksalderSpec(
    val simuleringType: SimuleringType,
    val sivilstand: Sivilstand? = null,
    val harEps: Boolean? = null, // EPS = ektefelle/partner/samboer
    val aarligInntektFoerUttak: Int? = null,
    val gradertUttak: GradertUttak? = null
)
