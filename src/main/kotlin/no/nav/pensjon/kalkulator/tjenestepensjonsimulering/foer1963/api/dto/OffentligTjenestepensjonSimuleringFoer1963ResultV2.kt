package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate

// Harmonized with OffentligTjenestepensjonSimuleringResultV2 structure
@JsonInclude(JsonInclude.Include.NON_NULL)
data class OffentligTjenestepensjonSimuleringFoer1963ResultV2(
    val simuleringsresultatStatus: SimuleringsresultatStatusV2 = SimuleringsresultatStatusV2.OK, // Domain today has no explicit status -> assume OK
    val muligeTpLeverandoerListe: List<String> = emptyList(), // Not available in resultat -> empty
    val simulertTjenestepensjon: SimulertTjenestepensjonFoer1963V2? = null,
    var serviceData: List<String>? = null // Not available -> null
)

// Nested structure analogous to SimulertTjenestepensjonV2 but with date-based period info
data class SimulertTjenestepensjonFoer1963V2(
    val tpLeverandoer: String?, // navnOrdning
    val tpNummer: String?,      // tpnr
    val simuleringsresultat: SimuleringsresultatFoer1963V2
)

data class SimuleringsresultatFoer1963V2(
    val utbetalingsperioder: List<UtbetalingsperiodeFoer1963V2>,
    val betingetTjenestepensjonErInkludert: Boolean = false // Not provided for før-1963 -> false
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UtbetalingsperiodeFoer1963V2(
    val datoFom: LocalDate? = null,
    val datoTom: LocalDate? = null,
    val grad: Int? = null,
    val arligUtbetaling: Double? = null,
    val ytelsekode: String? = null,
    val mangelfullSimuleringkode: String? = null
)
