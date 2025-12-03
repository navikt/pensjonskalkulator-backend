package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.Feilkode
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.YtelseskodeFoer1963
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.SimuleringsresultatStatusV2

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OffentligTjenestepensjonSimuleringFoer1963ResultV2(
    val simuleringsresultatStatus: SimuleringsresultatStatusV2 = SimuleringsresultatStatusV2.OK,
    val muligeTpLeverandoerListe: List<String> = emptyList(),
    val simulertTjenestepensjon: SimulertTjenestepensjonFoer1963V2? = null,
    var serviceData: List<String>? = null,
    val feilkode: Feilkode? = null,
)

data class SimulertTjenestepensjonFoer1963V2(
    val tpLeverandoer: String,
    val tpNummer: String,
    val simuleringsresultat: SimuleringsresultatFoer1963V2
)

data class SimuleringsresultatFoer1963V2(
    val utbetalingsperioder: List<UtbetalingsperiodeFoer1963V2>,
)

// Updated to same style as UtbetalingsperiodeV2 (fra1963) except maanedligUtbetaling not present
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UtbetalingsperiodeFoer1963V2(
    val startAlder: Alder,
    val sluttAlder: Alder?,
    val aarligUtbetaling: Int,
    val grad: Int?,
    val ytelsekode: YtelseskodeFoer1963,
    val mangelfullSimuleringkode: String?,
    val maanedligUtbetaling: Int?
)
