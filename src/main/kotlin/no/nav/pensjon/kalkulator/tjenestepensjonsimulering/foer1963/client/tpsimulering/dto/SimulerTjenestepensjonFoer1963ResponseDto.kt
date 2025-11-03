package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.tpsimulering.dto

data class SimulerTjenestepensjonFoer1963ResponseDto(
    val simulertPensjonListe: List<SimulertPensjon>
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
    val utbetalingsperioder: List<Utbetalingsperiode>?
)

data class Utbetalingsperiode(
    val datoFom: Long?, // Date as milliseconds since epoch
    val datoTom: Long?, // Date as milliseconds since epoch
    val grad: Int?,
    val arligUtbetaling: Double?,
    val ytelsekode: String?,
    val mangelfullSimuleringkode: String?
)