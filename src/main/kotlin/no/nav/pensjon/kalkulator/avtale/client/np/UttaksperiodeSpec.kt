package no.nav.pensjon.kalkulator.avtale.client.np

import no.nav.pensjon.kalkulator.avtale.Alder
import no.nav.pensjon.kalkulator.avtale.Uttaksgrad

data class UttaksperiodeSpec(
    val start: Alder,
    //val startAlder: Int,
    //val startMaaned: Int,
    val grad: Uttaksgrad,
    val aarligInntekt: Int
)
