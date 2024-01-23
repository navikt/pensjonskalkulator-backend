package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand

/**
 * Specifies personal parameters for 'simulering av alderspensjon'.
 * 'Personal' means parameters that require person ID to be known.
 */
data class PersonalSimuleringSpec(
    val pid: Pid,
    val sivilstand: Sivilstand,
    val aarligInntektFoerUttak: Int
    // NB: 'harEps' is not deduced from sivilstand, hence not considered to be 'personal'
    //     (unlike 'harEps' in PersonalUttaksalderSpec)
)
