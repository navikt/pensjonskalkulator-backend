package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.ResultatType

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OffentligTjenestepensjonSimuleringsresultatDtoV2 (
    val simuleringsresultatStatus: SimuleringsresultatStatusV2 = SimuleringsresultatStatusV2.OK,
    val muligeTpLeverandoerListe: List<String> = emptyList(),
    val simulertTjenestepensjon: SimulertTjenestepensjonV2? = null,
    var serviceData: List<String>? = null,
)

enum class SimuleringsresultatStatusV2(val resultatType: ResultatType?) {
    OK(ResultatType.OK),
    BRUKER_ER_IKKE_MEDLEM_AV_TP_ORDNING(ResultatType.IKKE_MEDLEM),
    TP_ORDNING_STOETTES_IKKE(ResultatType.TP_ORDNING_STOETTES_IKKE),
    TOM_SIMULERING_FRA_TP_ORDNING(ResultatType.TOM_RESPONS),
    TEKNISK_FEIL(null);

    companion object {
        fun fromResultatType(resultatType: ResultatType) = entries.firstOrNull { it.resultatType == resultatType } ?: TEKNISK_FEIL
    }
}

data class SimulertTjenestepensjonV2(
    val tpLeverandoer: String,
    val tpNummer: String,
    val simuleringsresultat: SimuleringsresultatV2
)

data class SimuleringsresultatV2(
    val utbetalingsperioder: List<UtbetalingsperiodeV2>,
    val betingetTjenestepensjonErInkludert: Boolean = false
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UtbetalingsperiodeV2(
    val startAlder: Alder,
    val sluttAlder: Alder?,
    val aarligUtbetaling: Int,
    val maanedtligUtbetaling: Int,
)