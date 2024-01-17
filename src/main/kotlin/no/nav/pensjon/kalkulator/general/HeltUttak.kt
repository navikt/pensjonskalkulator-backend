package no.nav.pensjon.kalkulator.general

/**
 * Helt uttak = uttak av full (100 %) alderpensjon.
 * Dette er en livsvarig ytelse (dermed ingen sluttdato for uttak).
 */
data class HeltUttak(
    val uttakFomAlder: Alder,
    val inntekt: Inntekt?
)

data class Inntekt(
    val aarligBeloep: Int,
    val tomAlder: Alder // tom = 'til og med'
    // 'fra og med'-alder = uttakFomAlder in parent class
)
