package no.nav.pensjon.kalkulator.general

/**
 * Alder i år og måneder.
 * Månedsverdi er 0 til 11 og betegner antall helt fylte måneder
 * (en alder av 62 år og 360 dager blir dermed "rundet av" nedover til 62 år og 11 måneder)
 */
data class Alder(val aar: Int, val maaneder: Int) {
    init {
        // Response form Norsk Pensjon violates this: require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
