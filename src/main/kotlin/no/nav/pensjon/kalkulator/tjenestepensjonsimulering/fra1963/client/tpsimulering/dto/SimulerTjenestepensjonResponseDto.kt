package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.tpsimulering.dto

import no.nav.pensjon.kalkulator.general.Alder

data class SimulerTjenestepensjonResponseDto(
    val simuleringsResultatStatus: SimuleringsResultatStatusDto,
    val simuleringsResultat: SimuleringsResultatDto? = null,
    val relevanteTpOrdninger: List<String> = emptyList(),
    var serviceData: List<String>? = emptyList(),
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
    TEKNISK_FEIL_FRA_TP_ORDNING,
}

data class SimuleringsResultatDto(
    val tpLeverandoer: String,
    val tpNummer: String,
    val utbetalingsperioder: List<UtbetalingPerAlder>,
    val betingetTjenestepensjonErInkludert: Boolean,
)

data class UtbetalingPerAlder(
    val startAlder: Alder,
    val sluttAlder: Alder?,
    val maanedligBeloep: Int,
)