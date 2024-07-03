package no.nav.pensjon.kalkulator.simulering.client.pen.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class SimuleringEgressSpecDto(
    val simuleringstype: String,
    val pid: String,
    val sivilstand: String,
    val epsHarPensjon: Boolean,
    val epsHarInntektOver2G: Boolean,
    val sisteInntekt: Int,
    val uttaksar: Int,
    val gradertUttak: GradertUttakSpecDto? = null,
    val heltUttak: HeltUttakSpecDto,
    val utenlandsperiodeListe: List<PenUtenlandsperiodeSpec>
)

data class GradertUttakSpecDto(
    val grad: String,
    val uttakFomAlder: AlderSpecDto,
    val aarligInntekt: Int
)

data class HeltUttakSpecDto(
    val uttakFomAlder: AlderSpecDto,
    val aarligInntekt: Int,
    val inntektTomAlder: AlderSpecDto
)

data class PenUtenlandsperiodeSpec (
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") val fom: LocalDate,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") val tom: LocalDate?,
    val land: String,
    val arbeidetUtenlands: Boolean
)

data class AlderSpecDto(val aar: Int, val maaneder: Int)
