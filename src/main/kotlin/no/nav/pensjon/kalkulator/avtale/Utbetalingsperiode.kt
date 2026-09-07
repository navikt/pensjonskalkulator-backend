package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad

/**
 * startAlder: Påkrevd; startalder
 * sluttAlder: Sluttalder; utelates hvis utbetalingen er livsvarig
 * aarligUtbetalingForventet: Årlig utbetaling forventet, basert på enten Norsk Pensjons prognose eller bransjemodellen, se felt «beregningsmodell»
 * grad: Påkrevd; hvis en rettighet ikke kan leveres med ønsket gradert beregning, så skal verdien til være 100 (for alle utbetalingsperiodene) og «Årsak til manglende gradering» ha en relevant feilkode.
 */
data class Utbetalingsperiode(
    val startAlder: Alder,
    val sluttAlder: Alder?,
    val aarligUtbetalingForventet: Int,
    val aarligUtbetalingNedreGrense: Int,
    val aarligUtbetalingOvreGrense: Int,
    val grad: Uttaksgrad
) {
    val erLivsvarig = sluttAlder == null

    init {
        require(startAlder lessThanOrEqualTo sluttAlder) { "startAlder <= sluttAlder" }
    }

    constructor(
        startAlder: Alder,
        sluttAlder: Alder?,
        aarligUtbetaling: Int,
        grad: Uttaksgrad
    ) :
            this(
                startAlder = startAlder,
                sluttAlder = sluttAlder,
                aarligUtbetalingForventet = aarligUtbetaling,
                aarligUtbetalingNedreGrense = 0,
                aarligUtbetalingOvreGrense = 0,
                grad = grad
            )
}
