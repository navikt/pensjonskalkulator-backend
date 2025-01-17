package no.nav.pensjon.kalkulator.simulering.client.simulator.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

// Corresponds to SimuleringEtter2011 in PEN
data class SimulatorAnonymSimuleringSpec (
    val simuleringType: String, // SimuleringTypeCode in PEN
    val fodselsar: Int,
    val sivilstatus: String, // SivilstatusTypeCode in PEN
    val eps2G: Boolean,
    val epsPensjon: Boolean,
    val utenlandsopphold: Int,
    val antArInntektOverG: Int,
    val forventetInntekt: Int,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET") val forsteUttakDato: LocalDate? = null,
    val utg: String, // UttaksgradCode in PEN
    val inntektUnderGradertUttak: Int? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET") val heltUttakDato: LocalDate? = null,
    val inntektEtterHeltUttak: Int,
    val antallArInntektEtterHeltUttak: Int
)

/* May be used in future instead of SimulatorAnonymSimuleringSpec:
data class SimulatorAnonymSimuleringSpec2(
    val simuleringType: String,
    val foedselAar: Int,
    val sivilstand: String,
    val epsHarPensjon: Boolean,
    val epsHarInntektOver2G: Boolean,
    val utenlandsAntallAar: Int,
    val inntektOver1GAntallAar: Int,
    val forventetAarligInntektFoerUttak: Int,
    val gradertUttak: SimulatorAnonymGradertUttakSpec? = null,
    val heltUttak: SimulatorAnonymHeltUttakSpec
)

data class SimulatorAnonymGradertUttakSpec(
    val grad: String,
    val uttakFomAlder: SimulatorAnonymAlderSpec,
    val aarligInntekt: Int
)

data class SimulatorAnonymHeltUttakSpec(
    val uttakFomAlder: SimulatorAnonymAlderSpec,
    val aarligInntekt: Int,
    val inntektTomAlder: SimulatorAnonymAlderSpec
)
*/
data class SimulatorAnonymAlderSpec(
    val aar: Int,
    val maaneder: Int
)
