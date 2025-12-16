package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.ResultatType

/**
 * Data transfer object for the result of 'simulering offentlig tjenestepensjon' version 2.
 * Changes must be coordinated with consumers of the API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class OffentligTjenestepensjonSimuleringResultV2 (
    @field:NotNull val simuleringsresultatStatus: SimuleringsresultatStatusV2 = SimuleringsresultatStatusV2.OK,
    @field:NotNull val muligeTpLeverandoerListe: List<String> = emptyList(),
    val simulertTjenestepensjon: SimulertTjenestepensjonV2? = null,
    var serviceData: List<String>? = null
)

enum class SimuleringsresultatStatusV2(val resultatType: ResultatType?) {
    OK(ResultatType.OK),
    BRUKER_ER_IKKE_MEDLEM_AV_TP_ORDNING(ResultatType.IKKE_MEDLEM),
    TP_ORDNING_STOETTES_IKKE(ResultatType.TP_ORDNING_STOETTES_IKKE),
    TOM_SIMULERING_FRA_TP_ORDNING(ResultatType.TOM_RESPONS),
    TEKNISK_FEIL(null);

    companion object {
        fun fromResultatType(type: ResultatType) = entries.firstOrNull { it.resultatType == type } ?: TEKNISK_FEIL
    }
}

data class SimulertTjenestepensjonV2(
    @field:NotNull val tpLeverandoer: String,
    @field:NotNull val tpNummer: String,
    @field:NotNull val simuleringsresultat: SimuleringsresultatV2
)

data class SimuleringsresultatV2(
    @field:NotNull val utbetalingsperioder: List<UtbetalingsperiodeV2>,
    @field:NotNull val betingetTjenestepensjonErInkludert: Boolean = false
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UtbetalingsperiodeV2(
    @field:NotNull val startAlder: Alder,
    val sluttAlder: Alder?,
    @field:NotNull val aarligUtbetaling: Int,
    val maanedligUtbetaling: Int?
)
