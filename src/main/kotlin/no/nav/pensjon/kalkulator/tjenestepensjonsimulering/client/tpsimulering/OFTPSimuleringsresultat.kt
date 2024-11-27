package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering

import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.dto.ResultatTypeDto

data class OFTPSimuleringsresultat(
    val simuleringsResultatStatus: SimuleringsResultatStatus,
    val simuleringsResultat: SimuleringsResultat? = null,
    val tpOrdninger: List<String> = emptyList(),
)

data class SimuleringsResultatStatus(
    val resultatType: ResultatType,
    val feilmelding: String? = null,
)

enum class ResultatType(val externalValue: ResultatTypeDto) {
    OK(ResultatTypeDto.SUCCESS),
    IKKE_MEDLEM(ResultatTypeDto.BRUKER_ER_IKKE_MEDLEM_HOS_TP_ORDNING),
    TP_ORDNING_STOETTES_IKKE(ResultatTypeDto.TP_ORDNING_ER_IKKE_STOTTET),
    TOM_RESPONS(ResultatTypeDto.INGEN_UTBETALINGSPERIODER_FRA_TP_ORDNING);

    companion object {
        fun fromExternalValue(externalValue: ResultatTypeDto) = entries.first { it.externalValue == externalValue }
    }
}

data class SimuleringsResultat(
    val tpOrdning: String,
    val perioder: List<Utbetaling>,
    val btpInkludert: Boolean,
)

data class Utbetaling(
    val aar: Int,
    val beloep: Int,
)