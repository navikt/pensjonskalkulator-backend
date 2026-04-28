package no.nav.pensjon.kalkulator.afp

import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import java.time.LocalDate

data class ServiceberegnetAfpResult(
    val afpOrdning: AfpOrdningType?,
    val beregnetAfp: BeregnetAfp?,
    val problem: ServiceberegnetAfpProblem?
)

data class BeregnetAfp(
    val totalbelopAfp: Int?,
    val virkFom: LocalDate?,
    val tidligereArbeidsinntekt: Int?,
    val grunnbelop: Int?,
    val sluttpoengtall: Double?,
    val trygdetid: Int?,
    val poengar: Int?,
    val poeangarF92: Int?,
    val poeangarE91: Int?,
    val grunnpensjon: Int?,
    val tilleggspensjon: Int?,
    val afpTillegg: Int?,
    val fpp: Double?,
    val sertillegg: Int?
)

data class ServiceberegnetAfpProblem(
    val type: ServiceberegnetAfpProblemType,
    val beskrivelse: String
)

enum class ServiceberegnetAfpProblemType {
    UTILSTREKKELIG_TRYGDETID,
    UTILSTREKKELIG_OPPTJENING,
    ANNEN_KLIENTFEIL
}
