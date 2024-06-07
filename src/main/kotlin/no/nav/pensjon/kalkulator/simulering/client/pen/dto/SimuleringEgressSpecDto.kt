package no.nav.pensjon.kalkulator.simulering.client.pen.dto

data class SimuleringEgressSpecDto(
    val simuleringstype: String,
    val pid: String,
    val sivilstand: String,
    val epsHarPensjon: Boolean,
    val epsHarInntektOver2G: Boolean,
    val sisteInntekt: Int,
    val uttaksar: Int,
    val gradertUttak: GradertUttakSpecDto? = null,
    val heltUttak: HeltUttakSpecDto
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

data class AlderSpecDto(val aar: Int, val maaneder: Int)
