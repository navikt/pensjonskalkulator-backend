package no.nav.pensjon.kalkulator.opptjening.behandlet

import no.nav.pensjon.kalkulator.general.Uttaksgrad

/**
 * Informasjon for merknader relatert til uttaksgrad.
 */
data class UttaksgradMerknad(
    val grad: Uttaksgrad,
    val kalendermaanedFom: Int? = null
)