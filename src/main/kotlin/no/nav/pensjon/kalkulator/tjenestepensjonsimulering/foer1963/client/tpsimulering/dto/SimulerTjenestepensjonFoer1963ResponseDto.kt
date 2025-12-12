package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto

data class SimulerTjenestepensjonFoer1963ResponseDto(
    val simulertPensjonListe: List<SimulertPensjon>,
    val feilrespons: FeilresponsDto?
)

data class FeilresponsDto(
    val errorCode: FeilkodeDto,
    val errorMessage: String,
)

data class SimulertPensjon(
    val tpnr: String?,
    val navnOrdning: String?,
    val inkluderteOrdninger: List<String>?,
    val leverandorUrl: String?,
    val inkluderteTpnr: List<String>?,
    val utelatteTpnr: List<String>?,
    val status: String?,
    val feilkode: String?,
    val feilbeskrivelse: String?,
    val utbetalingsperioder: List<Utbetalingsperiode> = emptyList()
)

data class Utbetalingsperiode(
    val datoFom: Long,
    val datoTom: Long?, // Date as milliseconds since epoch
    val grad: Int?,
    val arligUtbetaling: Double?,
    val ytelsekode: YtelsekodeFoer1963Dto?,
    val mangelfullSimuleringkode: String?
)

enum class YtelsekodeFoer1963Dto {
    AP,
    AFP,
    SERALDER
}

enum class FeilkodeDto {
    TEKNISK_FEIL,
    BEREGNING_GIR_NULL_UTBETALING,
    OPPFYLLER_IKKE_INNGANGSVILKAAR;
}