package no.nav.pensjon.kalkulator.simulering.client.simulator.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimulatorSimuleringSpec(
    val simuleringstype: String,
    val pid: String,
    val sivilstand: String,
    val epsHarPensjon: Boolean,
    val epsHarInntektOver2G: Boolean,
    val sisteInntekt: Int,
    val uttaksar: Int,
    val gradertUttak: SimulatorGradertUttakSpec? = null,
    val heltUttak: SimulatorHeltUttakSpec,
    val utenlandsperiodeListe: List<SimulatorUtlandPeriodeSpec>
)

data class SimulatorGradertUttakSpec(
    val grad: String,
    val uttakFomAlder: SimulatorAlderSpec,
    val aarligInntekt: Int
)

data class SimulatorHeltUttakSpec(
    val uttakFomAlder: SimulatorAlderSpec,
    val aarligInntekt: Int,
    val inntektTomAlder: SimulatorAlderSpec
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimulatorUtlandPeriodeSpec(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") val fom: LocalDate,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") val tom: LocalDate? = null,
    val land: String,
    val arbeidetUtenlands: Boolean
)

data class SimulatorAlderSpec(
    val aar: Int,
    val maaneder: Int
)
