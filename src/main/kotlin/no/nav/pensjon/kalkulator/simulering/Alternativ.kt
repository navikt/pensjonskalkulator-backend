package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad

data class Alternativ(
    val gradertUttakAlder: Alder? = null,
    val uttakGrad: Uttaksgrad? = null,
    val heltUttakAlder: Alder
)
