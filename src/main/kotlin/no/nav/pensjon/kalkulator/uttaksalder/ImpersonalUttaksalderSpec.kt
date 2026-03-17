package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.UttaksalderGradertUttak
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.simulering.InnvilgetLivsvarigOffentligAfpSpec
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.simulering.SimuleringType

/**
 * Specifies impersonal parameters for finding første mulige uttaksalder.
 * 'Impersonal' means parameters that are obtained without knowing the person's identity.
 */
data class ImpersonalUttaksalderSpec(
    val simuleringType: SimuleringType,
    val sivilstatus: Sivilstatus? = null,
    val harEps: Boolean? = null, // EPS = ektefelle/partner/samboer
    val aarligInntektFoerUttak: Int? = null,
    val gradertUttak: UttaksalderGradertUttak? = null,
    val heltUttak: HeltUttak?,
    val utenlandsperiodeListe: List<Opphold>,
    val innvilgetLivsvarigOffentligAfp: InnvilgetLivsvarigOffentligAfpSpec? = null
)
