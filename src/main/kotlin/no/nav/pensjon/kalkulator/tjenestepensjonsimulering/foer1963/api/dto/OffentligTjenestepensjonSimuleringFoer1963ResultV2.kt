package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OffentligTjenestepensjonSimuleringFoer1963ResultV2(
    val simuleringsresultatStatus: SimuleringsresultatStatusV2 = SimuleringsresultatStatusV2.OK,
    val muligeTpLeverandoerListe: List<String> = emptyList(),
    val simulertTjenestepensjon: SimulertTjenestepensjonFoer1963V2? = null,
    var serviceData: List<String>? = null
)

data class SimulertTjenestepensjonFoer1963V2(
    val tpLeverandoer: String,
    val tpNummer: String,
    val simuleringsresultat: SimuleringsresultatFoer1963V2
)

data class SimuleringsresultatFoer1963V2(
    val utbetalingsperioder: List<UtbetalingsperiodeFoer1963V2>,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UtbetalingsperiodeFoer1963V2(
    val datoFom: LocalDate?,
    val datoTom: LocalDate?,
    val grad: Int?,
    val arligUtbetaling: Double?,
    val ytelsekode: String?,
    val mangelfullSimuleringkode: String?
)
