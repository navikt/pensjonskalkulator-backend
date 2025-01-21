package no.nav.pensjon.kalkulator.simulering.client.simulator.dto

// Corresponds to NavSimuleringResultV3 in pensjonssimulator
// (and previously to SimuleringsresultatAlderspensjon1963Plus in PEN)
data class SimulatorPersonligSimuleringResult(
    val alderspensjonListe: List<SimulatorPersonligPensjon>,
    val alderspensjonMaanedsbeloep: SimulatorPersonligMaanedsbeloep?,
    val privatAfpListe: List<SimulatorPersonligPrivatAfp>,
    val livsvarigOffentligAfpListe: List<SimulatorPersonligLivsvarigOffentligAfp>,
    val vilkaarsproeving: SimulatorPersonligVilkaarsproeving?,
    val tilstrekkeligTrygdetidForGarantipensjon: Boolean?,
    val trygdetid: Int?,
    val opptjeningGrunnlagListe: List<SimulatorPersonligOpptjeningGrunnlag>?
)

data class SimulatorPersonligPensjon(
    val alderAar: Int,
    val beloep: Int,
    val inntektspensjon: Int?,
    val garantipensjon: Int?,
    val delingstall: Double?,
    val pensjonBeholdningFoerUttak: Int?
)

data class SimulatorPersonligMaanedsbeloep(
    val gradertUttakBeloep: Int?,
    val heltUttakBeloep: Int
)

data class SimulatorPersonligPrivatAfp(
    val alderAar: Int,
    val beloep: Int
)

data class SimulatorPersonligLivsvarigOffentligAfp(
    val alderAar: Int,
    val beloep: Int
)

data class SimulatorPersonligVilkaarsproeving(
    val vilkaarErOppfylt: Boolean,
    val alternativ: SimulatorPersonligAlternativ?
)

data class SimulatorPersonligOpptjeningGrunnlag(
    val aar: Int,
    val pensjonsgivendeInntektBeloep: Int
)

data class SimulatorPersonligAlternativ(
    val gradertUttakAlder: SimulatorPersonligAlder?,
    val uttaksgrad: Int?,
    val heltUttakAlder: SimulatorPersonligAlder
)

data class SimulatorPersonligAlder(
    val aar: Int,
    val maaneder: Int
)
