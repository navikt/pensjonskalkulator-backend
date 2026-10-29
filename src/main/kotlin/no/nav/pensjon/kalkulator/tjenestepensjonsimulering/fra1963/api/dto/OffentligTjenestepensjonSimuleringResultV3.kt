package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.ResultatType

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OffentligTjenestepensjonSimuleringResultV3 (
    @field:NotNull val simuleringsresultatStatus: SimuleringsresultatStatusV3 = SimuleringsresultatStatusV3.OK,
    @field:NotNull val muligeTpLeverandoerListe: List<TpOrdningV3> = emptyList(),
    val simulertTjenestepensjon: SimulertTjenestepensjonV3? = null,
    var serviceData: List<String>? = null
)

enum class SimuleringsresultatStatusV3(val resultatType: ResultatType?) {
    OK(ResultatType.OK),
    BRUKER_ER_IKKE_MEDLEM_AV_TP_ORDNING(ResultatType.IKKE_MEDLEM),
    TP_ORDNING_STOETTES_IKKE(ResultatType.TP_ORDNING_STOETTES_IKKE),
    TOM_SIMULERING_FRA_TP_ORDNING(ResultatType.TOM_RESPONS),
    TEKNISK_FEIL(null);

    companion object {
        fun fromResultatType(type: ResultatType) = entries.firstOrNull { it.resultatType == type } ?: TEKNISK_FEIL
    }
}

data class SimulertTjenestepensjonV3(
    @field:NotNull val tpLeverandoer: String,
    @field:NotNull val tpOrdning: TpOrdningV3,
    @field:NotNull val simuleringsresultat: SimuleringsresultatV3
)

data class SimuleringsresultatV3(
    @field:NotNull val utbetalingsperioder: List<UtbetalingsperiodeV3>,
    @field:NotNull val betingetTjenestepensjonErInkludert: Boolean = false
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UtbetalingsperiodeV3(
    @field:NotNull val startAlder: Alder,
    val sluttAlder: Alder?,
    @field:NotNull val aarligUtbetaling: Int,
    val maanedligUtbetaling: Int?
)

data class TpOrdningV3(
    @field:NotNull val tpNummer: String,
    @field:NotNull val tpNavn: String
)
