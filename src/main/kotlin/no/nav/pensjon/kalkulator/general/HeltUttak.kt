package no.nav.pensjon.kalkulator.general

/**
 * Helt uttak = uttak av full (100 %) alderpensjon.
 * Dette er en livsvarig ytelse (dermed ingen sluttdato for uttak).
 */
data class HeltUttak(
    val uttakFomAlder: Alder?, // fom = 'fra og med'; optional since not known in context of 'finn første mulige uttaksalder ved helt uttak'
    val inntekt: Inntekt? // optional since bruker may not have 'inntekt under helt uttak'
) {
    init {
        // Values may be null in different contexts, but not at the same time
        // (in that case the entire object should be null instead)
        require(uttakFomAlder != null || inntekt != null) { "uttakFomAlder and inntekt cannot both be null" }
    }
}

data class Inntekt(
    val aarligBeloep: Int,
    val tomAlder: Alder // tom = 'til og med'
    // 'fra og med'-alder = uttakFomAlder in parent class
)
