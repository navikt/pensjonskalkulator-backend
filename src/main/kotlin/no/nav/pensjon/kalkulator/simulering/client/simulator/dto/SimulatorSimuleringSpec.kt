package no.nav.pensjon.kalkulator.simulering.client.simulator.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import no.nav.pensjon.kalkulator.person.Pid.Companion.redact
import java.time.LocalDate

@JsonInclude(NON_NULL)
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
    val utenlandsperiodeListe: List<SimulatorUtlandPeriodeSpec>,
    val afpInntektMaanedFoerUttak: Boolean?,
    val afpOrdning: String? = null,
    val innvilgetLivsvarigOffentligAfp: SimulatorInnvilgetLivsvarigOffentligAfpSpec? = null
) {
    /**
     * toString with redacted person ID
     */
    override fun toString() =
        "pid: ${redact(pid)}, " +
                "simuleringstype: $simuleringstype, " +
                "sivilstand: $sivilstand, " +
                "epsHarPensjon: $epsHarPensjon, " +
                "epsHarInntektOver2G: $epsHarInntektOver2G, " +
                "sisteInntekt: $sisteInntekt, " +
                "uttaksar: $uttaksar, " +
                "gradertUttak: $gradertUttak, " +
                "heltUttak: $heltUttak, " +
                "utenlandsperiodeListe: $utenlandsperiodeListe, " +
                "afpInntektMaanedFoerUttak: $afpInntektMaanedFoerUttak, " +
                "afpOrdning: $afpOrdning, " +
                "innvilgetLivsvarigOffentligAfp: $innvilgetLivsvarigOffentligAfp"
}

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

@JsonInclude(NON_NULL)
data class SimulatorUtlandPeriodeSpec(
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val fom: LocalDate,
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val tom: LocalDate? = null,
    val land: String,
    val arbeidetUtenlands: Boolean
)

/**
 * Spesifiserer egenskapene til en løpende livsvarig AFP i offentlig sektor.
 */
@JsonInclude(NON_NULL)
data class SimulatorInnvilgetLivsvarigOffentligAfpSpec(
    val aarligBruttoBeloep: Double,
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val uttakFom: LocalDate,
    val sistRegulertGrunnbeloep: Int? = null
)

data class SimulatorAlderSpec(
    val aar: Int,
    val maaneder: Int
)
