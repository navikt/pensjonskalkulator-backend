package no.nav.pensjon.kalkulator.avtale.client.np

import no.nav.pensjon.kalkulator.person.Pid

data class PensjonsavtaleSpec(
    val pid: Pid,
    val aarligInntektFoerUttak: Int,
    val uttaksperioder: List<UttaksperiodeSpec>,
    val antallInntektsaarEtterUttak: Int
)
