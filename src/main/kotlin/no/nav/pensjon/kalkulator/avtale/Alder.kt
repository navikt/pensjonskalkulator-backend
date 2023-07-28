package no.nav.pensjon.kalkulator.avtale

/**
 * Beskrivelse av felter:
 * aar: Påkrevd; antall fylte hele år
 * maaned: Påkrevd; måned (1..12). Verdien er relativ til, og etter, brukerens fødselsmåned.
 *         F.eks., for en bruker født i april (4), så skal startmåned 2 tolkes som førstkommende juni (6 = 4 + 2)
 *         etter at brukeren har fylt startalder. Dette kan bety en reell dato i året etter at brukeren har fylt startalder.
 */
data class Alder(
    val aar: Int,
    val maaned: Int
)
