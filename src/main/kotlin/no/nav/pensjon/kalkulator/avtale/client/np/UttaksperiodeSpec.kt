package no.nav.pensjon.kalkulator.avtale.client.np

import no.nav.pensjon.kalkulator.avtale.Alder
import no.nav.pensjon.kalkulator.avtale.Uttaksgrad

/**
 * Beskrivelse av felter:
 * start: Må resultere i en dato tidligst inneværende måned kallet blir gjort.
 *        Månedsverdien i startalder er relativ til, og etter, brukerens fødselsmåned.
 *        F.eks., for en bruker født i april (4), så skal startmåned 2 tolkes som førstkommende juni (6 = 4 + 2) etter at brukeren har fylt startalder.
 *        Dette kan bety en reell dato i året etter at brukeren har fylt startalder.
 * grad: Uttaksgrad; i den siste uttaksperioden må grad være 100.
 * aarligInntekt: Årlig inntekt i uttaksperioden.
 */
data class UttaksperiodeSpec(
    val start: Alder,
    val grad: Uttaksgrad,
    val aarligInntekt: Int
)
