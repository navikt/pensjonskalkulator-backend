package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.Feilkode
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.YtelseskodeFoer1963
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.SimuleringsresultatStatusV2

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OffentligTjenestepensjonSimuleringFoer1963ResultV1(
    @field:NotNull val simuleringsresultatStatus: SimuleringsresultatStatusV2 = SimuleringsresultatStatusV2.OK,
    @field:NotNull val muligeTpLeverandoerListe: List<String> = emptyList(),
    val simulertTjenestepensjon: SimulertTjenestepensjonFoer1963V1? = null,
    var serviceData: List<String>? = null,
    val feilkode: Feilkode? = null,
)

data class SimulertTjenestepensjonFoer1963V1(
    @field:NotNull val tpLeverandoer: String,
    @field:NotNull val tpNummer: String,
    @field:NotNull val simuleringsresultat: SimuleringsresultatFoer1963V1
)

data class SimuleringsresultatFoer1963V1(
    @field:NotNull val utbetalingsperioder: List<UtbetalingsperiodeFoer1963V1>,
)

// Updated to same style as UtbetalingsperiodeV2 (fra1963) except maanedligUtbetaling not present
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UtbetalingsperiodeFoer1963V1(
    @field:NotNull val startAlder: Alder,
    val sluttAlder: Alder?,
    @field:NotNull val aarligUtbetaling: Int,
    val grad: Int?,
    @field:NotNull val ytelsekode: YtelseskodeFoer1963,
    val mangelfullSimuleringkode: String?,
    val maanedligUtbetaling: Int?
)
