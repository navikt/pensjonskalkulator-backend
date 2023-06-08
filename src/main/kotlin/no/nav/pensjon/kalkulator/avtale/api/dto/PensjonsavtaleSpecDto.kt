package no.nav.pensjon.kalkulator.avtale.api.dto

import no.nav.pensjon.kalkulator.avtale.client.np.UttaksperiodeSpec

data class PensjonsavtaleSpecDto(
    val aarligInntektFoerUttak: Int,
    val uttaksperiode: UttaksperiodeSpec,
    val antallInntektsaarEtterUttak: Int
)
