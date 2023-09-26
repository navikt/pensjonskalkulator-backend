package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad

/**
 * start: Påkrevd; startalder
 * slutt: Sluttalder; utelates hvis utbetalingen er livsvarig
 * aarligUtbetalingForventet: Årlig utbetaling forventet, basert på enten Norsk Pensjons prognose eller bransjemodellen, se felt «beregningsmodell»
 * grad: Påkrevd; hvis en rettighet ikke kan leveres med ønsket gradert beregning, så skal verdien til være 100 (for alle utbetalingsperiodene) og «Årsak til manglende gradering» ha en relevant feilkode.
 */
data class Utbetalingsperiode(
    val start: Alder,
    val slutt: Alder?,
    val aarligUtbetalingForventet: Int,
    val aarligUtbetalingNedreGrense: Int,
    val aarligUtbetalingOvreGrense: Int,
    val grad: Uttaksgrad
) {
    val erLivsvarig = slutt == null

    constructor(
        start: Alder,
        slutt: Alder?,
        aarligUtbetaling: Int,
        grad: Uttaksgrad
    ) :
            this(
                start = start,
                slutt = slutt,
                aarligUtbetalingForventet = aarligUtbetaling,
                aarligUtbetalingNedreGrense = 0,
                aarligUtbetalingOvreGrense = 0,
                grad = grad
            )
}
