package no.nav.pensjon.kalkulator.merknad

import no.nav.pensjon.kalkulator.general.Uttaksgrad

data class UttaksgradMerknad(
    val grad: Uttaksgrad,
    val kalendermaanedFom: Int? = null
)