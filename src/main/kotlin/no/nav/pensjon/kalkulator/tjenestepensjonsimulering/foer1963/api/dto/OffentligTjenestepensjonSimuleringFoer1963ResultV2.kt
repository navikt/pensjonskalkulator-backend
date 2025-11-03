package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto

import java.time.LocalDate

data class OffentligTjenestepensjonSimuleringFoer1963ResultV2 (
    val tpnr: String? = null,
    val navnOrdning: String? = null,
    val utbetalingsperioder: List<UtbelatingsperiodeFoer1963ResultV2>? = null
)

data class UtbelatingsperiodeFoer1963ResultV2(
    val datoFom: LocalDate? = null,
    val datoTom: LocalDate? = null,
    val grad: Int? = null,
    val arligUtbetaling: Double? = null,
    val ytelsekode: String? = null,
    val mangelfullSimuleringkode: String? = null
)
