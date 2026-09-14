package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto

import java.time.LocalDate

/**
 * Korresponderer med ServiceberegningAfpResultDto i pensjonssimulator.
 */
data class ServiceberegningAfpResultDto(
    val beregnetAfp: ServiceberegningFolketrygdberegnetAfpDto?,
    val opptjeningListe: List<ServiceberegningOpptjeningDto>,
    val problem: ServiceberegningProblemDto? = null
)

data class ServiceberegningFolketrygdberegnetAfpDto(
    val afpTotalbeloep: Int,
    val virkningFom: LocalDate?,
    val tidligereArbeidsinntekt: Int?,
    val grunnbeloep: Int?,
    val sluttpoengtall: Double?,
    val trygdetid: Int?,
    val poengaar: Int?,
    val poengaarFoer1992: Int?,
    val poengaarEtter1991: Int?,
    val grunnpensjon: Int?,
    val tilleggspensjon: Int?,
    val afpTillegg: Int?,
    val fpp: Double?,
    val saertillegg: Int?,
    val grad: Int?,
    val erAvkortet: Boolean?
)

data class ServiceberegningOpptjeningDto(
    val aarstall: Int,
    val pensjonsgivendeInntekt: Int,
    val pensjonspoeng: Double
)

data class ServiceberegningProblemDto(
    val type: String, // ServiceberegningProblemtypeDto
    val beskrivelse: String
)