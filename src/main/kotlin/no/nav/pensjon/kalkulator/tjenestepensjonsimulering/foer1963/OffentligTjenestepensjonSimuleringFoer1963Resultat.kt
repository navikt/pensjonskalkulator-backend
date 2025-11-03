package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963

import java.time.LocalDate

data class OffentligTjenestepensjonSimuleringFoer1963Resultat(
    val tpnr: String? = null,
    val navnOrdning: String? = null,
    val utbetalingsperioder: List<UtbetalingsperiodeResultat>? = null
)

data class UtbetalingsperiodeResultat(
    val datoFom: LocalDate? = null,
    val datoTom: LocalDate? = null,
    val grad: Int? = null,
    val arligUtbetaling: Double? = null,
    val ytelsekode: String? = null,
    val mangelfullSimuleringkode: String? = null
)