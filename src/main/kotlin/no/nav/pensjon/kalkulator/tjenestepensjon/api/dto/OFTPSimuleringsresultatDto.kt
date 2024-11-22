package no.nav.pensjon.kalkulator.tjenestepensjon.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OFTPSimuleringsresultatDto (
    val simuleringsresultatStatus: SimuleringsresultatStatus = SimuleringsresultatStatus.OK,
    val muligeTpLeverandoerListe: List<String> = emptyList(),
    val simulertTjenestepensjon: SimulertTjenestepensjon? = null,
)

enum class SimuleringsresultatStatus {
    OK,
    BRUKER_ER_IKKE_MEDLEM_AV_TP_ORDNING,
    TP_ORDNING_STOETTES_IKKE,
    TEKNISK_FEIL,
}

data class SimulertTjenestepensjon(
    val tpLeverandoer: String,
    val simuleringsresultat: Simuleringsresultat
)

data class Simuleringsresultat(
    val utbetalingsperioder: List<UtbetalingPerAar>,
    val betingetTjenestepensjonErInkludert: Boolean = false
)

data class UtbetalingPerAar(
    val aar: Int,
    val beloep: Int,
)