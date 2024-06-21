package no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto

data class PenUttaksalderSpec(
    val pid: String,
    val sivilstand: String,
    val harEps: Boolean,
    val sisteInntekt: Int,
    val simuleringType: String,
    val gradertUttak: PenUttaksalderGradertUttakSpec? = null,
    val heltUttak: PenUttaksalderHeltUttakSpec
)

/**
 * Notable difference compared to GradertUttakSpecDto (for simulering):
 * - uttakFomAlder is missing (because uttakFomAlder is the 'første mulige uttaksalder' to be found)
 */
data class PenUttaksalderGradertUttakSpec(
    val grad: String,
    val aarligInntekt: Int?
)

/**
 * Notable difference compared to HeltUttakSpecDto (for simulering):
 * - uttakFomAlder is optional (because uttakFomAlder is only required if 'gradert uttak',
 *     if 'helt uttak' then uttakFomAlder is the 'første mulige uttaksalder' to be found)
 */
data class PenUttaksalderHeltUttakSpec(
    val uttakFomAlder: PenUttaksalderAlderSpec?,
    val inntekt: PenUttaksalderInntektSpec?
)

data class PenUttaksalderInntektSpec(
    val aarligBelop: Int,
    val tomAlder: PenUttaksalderAlderSpec
)

data class PenUttaksalderAlderSpec(
    val aar: Int,
    val maaneder: Int
)
