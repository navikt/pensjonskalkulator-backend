package no.nav.pensjon.kalkulator.afp.api.dto

import java.time.LocalDate

data class InternServiceberegnetAfpResult(
    val afpOrdning: String?,
    val beregnetAfp: InternBeregnetAfp?,
    val problem: InternServiceberegnetAfpProblem?
)

data class InternBeregnetAfp(
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

data class InternServiceberegnetAfpProblem(
    val type: String?,
    val beskrivelse: String?
)
