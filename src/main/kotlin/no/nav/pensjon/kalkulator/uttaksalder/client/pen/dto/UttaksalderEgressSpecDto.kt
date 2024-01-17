package no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto

import java.util.*

data class UttaksalderEgressSpecDto(
    val pid: String,
    val sivilstand: String,
    val harEps: Boolean,
    val sisteInntekt: Int,
    val uttaksgrad: String? = null, // deprecated - replaced by gradertUttak.grad
    val heltUttakDato: Date? = null, // deprecated - replaced by heltUttak.uttakFomAlder
    val simuleringType: String,
    val gradertUttak: UttaksalderGradertUttakSpecDto? = null,
    val heltUttak: UttaksalderHeltUttakSpecDto? = null
)

/**
 * Notable difference compared to GradertUttakSpecDto (for simulering):
 * - uttakFomAlder is missing (because uttakFomAlder is the 'første mulige uttaksalder' to be found)
 */
data class UttaksalderGradertUttakSpecDto(
    val grad: String,
    val aarligInntekt: Int?
)

/**
 * Notable difference compared to HeltUttakSpecDto (for simulering):
 * - uttakFomAlder is optional (because uttakFomAlder is only required if 'gradert uttak',
 *     if 'helt uttak' then uttakFomAlder is the 'første mulige uttaksalder' to be found)
 */
data class UttaksalderHeltUttakSpecDto(
    val uttakFomAlder: UttaksalderAlderDto?,
    val inntekt: UttaksalderInntektDto?
)

data class UttaksalderInntektDto(
    val aarligBelop: Int,
    val tomAlder: UttaksalderAlderDto
)

data class UttaksalderAlderDto(val aar: Int, val maaneder: Int)
