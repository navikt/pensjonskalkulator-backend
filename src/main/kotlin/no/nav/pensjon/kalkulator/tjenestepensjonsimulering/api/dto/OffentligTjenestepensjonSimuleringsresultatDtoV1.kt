package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.ResultatType

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OffentligTjenestepensjonSimuleringsresultatDtoV1 (
    val simuleringsresultatStatus: SimuleringsresultatStatusV1 = SimuleringsresultatStatusV1.OK,
    val muligeTpLeverandoerListe: List<String> = emptyList(),
    val simulertTjenestepensjon: SimulertTjenestepensjonV1? = null,
)

enum class SimuleringsresultatStatusV1(val resultatType: ResultatType?) {
    OK(ResultatType.OK),
    BRUKER_ER_IKKE_MEDLEM_AV_TP_ORDNING(ResultatType.IKKE_MEDLEM),
    TP_ORDNING_STOETTES_IKKE(ResultatType.TP_ORDNING_STOETTES_IKKE),
    TOM_SIMULERING_FRA_TP_ORDNING(ResultatType.TOM_RESPONS),
    TEKNISK_FEIL(null);

    companion object {
        fun fromResultatType(resultatType: ResultatType) = entries.firstOrNull { it.resultatType == resultatType } ?: TEKNISK_FEIL
    }
}

data class SimulertTjenestepensjonV1(
    val tpLeverandoer: String,
    val simuleringsresultat: SimuleringsresultatV1
)

data class SimuleringsresultatV1(
    val utbetalingsperioder: List<UtbetalingsperiodeV1>,
    val betingetTjenestepensjonErInkludert: Boolean = false
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UtbetalingsperiodeV1(
    val startAlder: Alder,
    val sluttAlder: Alder?,
    val aarligUtbetaling: Int,
)