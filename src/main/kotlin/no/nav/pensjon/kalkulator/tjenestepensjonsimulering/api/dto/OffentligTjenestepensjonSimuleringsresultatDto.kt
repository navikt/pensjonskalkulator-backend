package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.ResultatType

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OffentligTjenestepensjonSimuleringsresultatDto (
    val simuleringsresultatStatus: SimuleringsresultatStatus = SimuleringsresultatStatus.OK,
    val muligeTpLeverandoerListe: List<String> = emptyList(),
    val simulertTjenestepensjon: SimulertTjenestepensjon? = null,
)

enum class SimuleringsresultatStatus(val resultatType: ResultatType?) {
    OK(ResultatType.OK),
    BRUKER_ER_IKKE_MEDLEM_AV_TP_ORDNING(ResultatType.IKKE_MEDLEM),
    TP_ORDNING_STOETTES_IKKE(ResultatType.TP_ORDNING_STOETTES_IKKE),
    TOM_SIMULERING_FRA_TP_ORDNING(ResultatType.TOM_RESPONS),
    TEKNISK_FEIL(null);

    companion object {
        fun fromResultatType(resultatType: ResultatType) = entries.firstOrNull { it.resultatType == resultatType } ?: TEKNISK_FEIL
    }
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