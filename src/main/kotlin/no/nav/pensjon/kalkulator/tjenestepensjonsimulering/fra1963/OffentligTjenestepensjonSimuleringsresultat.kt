package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.tpsimulering.dto.ResultatTypeDto

data class OffentligTjenestepensjonSimuleringsresultat(
    val simuleringsResultatStatus: SimuleringsResultatStatus,
    val simuleringsResultat: SimuleringsResultat? = null,
    val tpOrdninger: List<String> = emptyList(),
    var serviceData: List<String> = emptyList(),
)

data class SimuleringsResultatStatus(
    val resultatType: ResultatType,
    val feilmelding: String? = null,
)

enum class ResultatType(val externalValue: ResultatTypeDto) {
    OK(ResultatTypeDto.SUCCESS),
    IKKE_MEDLEM(ResultatTypeDto.BRUKER_ER_IKKE_MEDLEM_HOS_TP_ORDNING),
    TP_ORDNING_STOETTES_IKKE(ResultatTypeDto.TP_ORDNING_ER_IKKE_STOTTET),
    TOM_RESPONS(ResultatTypeDto.INGEN_UTBETALINGSPERIODER_FRA_TP_ORDNING),
    TEKNISK_FEIL(ResultatTypeDto.TEKNISK_FEIL_FRA_TP_ORDNING);

    companion object {
        fun fromExternalValue(externalValue: ResultatTypeDto) = entries.first { it.externalValue == externalValue }
    }
}

data class SimuleringsResultat(
    val tpOrdning: String,
    val tpNummer: String,
    val perioder: List<Utbetaling>,
    val betingetTjenestepensjonInkludert: Boolean,
)

data class Utbetaling(
    val startAlder: Alder,
    val sluttAlder: Alder?,
    val maanedligBeloep: Int,
)
