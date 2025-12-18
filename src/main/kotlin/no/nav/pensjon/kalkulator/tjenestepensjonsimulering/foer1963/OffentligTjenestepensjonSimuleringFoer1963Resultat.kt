package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.FeilkodeDto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.YtelsekodeFoer1963Dto

data class OffentligTjenestepensjonSimuleringFoer1963Resultat(
    val tpnr: String? = null,
    val navnOrdning: String? = null,
    val utbetalingsperioder: List<UtbetalingsperiodeResultat> = emptyList(),
    val feilkode: Feilkode? = null,
    val relevanteTpOrdninger: List<String>? = emptyList()
)

data class UtbetalingsperiodeResultat(
    val alderFom: Alder,
    val alderTom: Alder? = null,
    val grad: Int? = null,
    val arligUtbetaling: Double? = null,
    val ytelsekode: YtelseskodeFoer1963,
    val mangelfullSimuleringkode: String? = null
)


enum class YtelseskodeFoer1963(val externalValue: YtelsekodeFoer1963Dto) {
    AP(YtelsekodeFoer1963Dto.AP), // Alderspensjon (tjenestepensjon)
    AFP(YtelsekodeFoer1963Dto.AFP), // Avtalefestet pensjon
    SERALDER(YtelsekodeFoer1963Dto.SERALDER); //Alderspensjon med særaldersgrense

    companion object {
        fun fromExternalValue(externalValue: YtelsekodeFoer1963Dto) = entries.first { it.externalValue == externalValue }
    }
}

enum class Feilkode(val externalValue: FeilkodeDto) {
    TEKNISK_FEIL(FeilkodeDto.TEKNISK_FEIL),
    BEREGNING_GIR_NULL_UTBETALING(FeilkodeDto.BEREGNING_GIR_NULL_UTBETALING),
    BRUKER_IKKE_MEDLEM_AV_TP_ORDNING(FeilkodeDto.BRUKER_IKKE_MEDLEM_AV_TP_ORDNING),
    TP_ORDNING_STOETTES_IKKE(FeilkodeDto.TP_ORDNING_STOETTES_IKKE),
    OPPFYLLER_IKKE_INNGANGSVILKAAR(FeilkodeDto.OPPFYLLER_IKKE_INNGANGSVILKAAR);

    companion object {
        fun fromExternalValue(externalValue: FeilkodeDto) = entries.first { it.externalValue == externalValue }
    }
}