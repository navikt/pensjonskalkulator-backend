package no.nav.pensjon.kalkulator.avtale.client.np

import no.nav.pensjon.kalkulator.person.Pid

data class PensjonsavtaleSpec(
    val pid: Pid,
    val aarligInntektFoerUttak: Int,
    val uttaksperiode: UttaksperiodeSpec,
    val antallInntektsaarEtterUttak: Int
)
