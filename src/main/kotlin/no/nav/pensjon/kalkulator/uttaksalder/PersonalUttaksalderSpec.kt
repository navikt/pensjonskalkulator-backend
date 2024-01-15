package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand

/**
 * Specifies personal parameters for finding første mulige uttaksalder.
 * 'Personal' means parameters that require person ID to be known.
 */
data class PersonalUttaksalderSpec(
    val pid: Pid,
    val sivilstand: Sivilstand,
    val harEps: Boolean, // EPS = ektefelle/partner/samboer
    val aarligInntektFoerUttak: Int
)
