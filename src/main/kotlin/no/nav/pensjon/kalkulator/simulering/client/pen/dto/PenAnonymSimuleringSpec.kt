package no.nav.pensjon.kalkulator.simulering.client.pen.dto

data class PenAnonymSimuleringSpec(
    val simuleringType: String,
    val foedselAar: Int,
    val sivilstand: String,
    val epsHarPensjon: Boolean,
    val epsHarInntektOver2G: Boolean,
    val utenlandsAntallAar: Int,
    val inntektOver1GAntallAar: Int,
    val forventetAarligInntektFoerUttak: Int,
    val gradertUttak: PenAnonymGradertUttakSpec? = null,
    val heltUttak: PenAnonymHeltUttakSpec
)

data class PenAnonymGradertUttakSpec(
    val grad: String,
    val uttakFomAlder: PenAnonymAlderSpec,
    val aarligInntekt: Int
)

data class PenAnonymHeltUttakSpec(
    val uttakFomAlder: PenAnonymAlderSpec,
    val aarligInntekt: Int,
    val inntektTomAlder: PenAnonymAlderSpec
)

data class PenAnonymAlderSpec(
    val aar: Int,
    val maaneder: Int
)
