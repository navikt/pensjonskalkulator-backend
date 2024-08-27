package no.nav.pensjon.kalkulator.simulering.client.pen.dto

import java.time.LocalDate

// Corresponds to SimuleringEtter2011 in PEN
data class PenAnonymSimuleringSpec (
    val simuleringType: String, // SimuleringTypeCode in PEN
    val fodselsar: Int,
    val sivilstatus: String, // SivilstatusTypeCode in PEN
    val eps2G: Boolean,
    val epsPensjon: Boolean,
    val utenlandsopphold: Int,
    val antArInntektOverG: Int,
    val forventetInntekt: Int,
    val forsteUttakDato: LocalDate,
    val utg: String, // UttaksgradCode in PEN
    val inntektUnderGradertUttak: Int? = null,
    val heltUttakDato: LocalDate? = null,
    val inntektEtterHeltUttak: Int,
    val antallArInntektEtterHeltUttak: Int
)

/* May be used in future instead of PenAnonymSimuleringSpec:
data class PenAnonymSimuleringSpec2(
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
*/
data class PenAnonymAlderSpec(
    val aar: Int,
    val maaneder: Int
)
