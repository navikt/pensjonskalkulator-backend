package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.dto

data class SimulerTjenestepensjonResponseDto(
    val simuleringsResultatStatus: SimuleringsResultatStatusDto,
    val simuleringsResultat: SimuleringsResultatDto? = null,
    val relevanteTpOrdninger: List<String> = emptyList(),
)

data class SimuleringsResultatStatusDto(
    val resultatType: ResultatTypeDto,
    val feilmelding: String? = null,
)

enum class ResultatTypeDto {
    SUCCESS,
    BRUKER_ER_IKKE_MEDLEM_HOS_TP_ORDNING,
    TP_ORDNING_ER_IKKE_STOTTET,
    INGEN_UTBETALINGSPERIODER_FRA_TP_ORDNING,
}

data class SimuleringsResultatDto(
    val tpLeverandoer: String,
    val utbetalingsperioder: List<UtbetalingPerAar>,
    val betingetTjenestepensjonErInkludert: Boolean,
)

data class UtbetalingPerAar(
    val aar: Int,
    val beloep: Int,
)