package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType

data class ImpersonalUttaksalderSpec(
    val sivilstand: Sivilstand?,
    val harEps: Boolean?, // EPS = ektefelle/partner/samboer
    val sisteInntekt: Int?,
    val simuleringType: SimuleringType?,
    val gradertUttak: GradertUttak? = null
)
