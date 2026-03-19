package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstatus

/**
 * Specifies personal parameters for 'simulering av alderspensjon'.
 * 'Personal' means parameters that require person ID to be known.
 */
data class PersonalSimuleringSpec(
    val pid: Pid,
    val sivilstatus: Sivilstatus,
    val aarligInntektFoerUttak: Int
    // NB: 'harEps' is not deduced from sivilstatus, hence not considered to be 'personal'
    //     (unlike 'harEps' in PersonalUttaksalderSpec)
)
