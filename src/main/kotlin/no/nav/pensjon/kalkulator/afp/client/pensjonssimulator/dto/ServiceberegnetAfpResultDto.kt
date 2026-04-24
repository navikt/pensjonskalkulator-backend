package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto

import java.time.LocalDate

data class ServiceberegnetAfpResultDto(
    val afpOrdning: String?,
    val beregnetAfp: BeregnetAfpDto?,
    val problem: ServiceberegnetAfpProblemDto?
)

data class BeregnetAfpDto(
    val totalbelopAfp: Int?,
    val virkFom: LocalDate?,
    val tidligereArbeidsinntekt: Int?,
    val grunnbelop: Int?,
    val sluttpoengtall: Double?,
    val trygdetid: Int?,
    val poengar: Int?,
    val poeangar_f92: Int?,
    val poeangar_e91: Int?,
    val grunnpensjon: Int?,
    val tilleggspensjon: Int?,
    val afpTillegg: Int?,
    val fpp: Double?,
    val sertillegg: Int?
)

data class ServiceberegnetAfpProblemDto(
    val type: String?,
    val beskrivelse: String?
)
