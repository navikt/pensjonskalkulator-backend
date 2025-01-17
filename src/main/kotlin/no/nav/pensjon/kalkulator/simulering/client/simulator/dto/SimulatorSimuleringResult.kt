package no.nav.pensjon.kalkulator.simulering.client.simulator.dto

// Corresponds to SimuleringsresultatAlderspensjon1963Plus in PEN
data class SimulatorSimuleringResult(
    val alderspensjon: List<SimulatorPensjon>,
    val alderspensjonMaanedsbeloep: SimulatorMaanedsbeloep,
    val afpPrivat: List<SimulatorPensjon>,
    val afpOffentliglivsvarig: List<SimulatorPensjonAfpOffentlig>,
    val vilkaarsproeving: SimulatorVilkaarsproeving?,
    val harNokTrygdetidForGarantipensjon: Boolean?,
    val trygdetid: Int?,
    val opptjeningGrunnlagListe: List<SimulatorOpptjeningGrunnlag>?
)

data class SimulatorPensjon(
    val alder: Int,
    val beloep: Int,
    val inntektspensjon: Int?,
    val garantipensjon: Int?,
    val delingstall: Double?,
    val pensjonBeholdningFoerUttak: Int?
)

data class SimulatorPensjonAfpOffentlig(
    val alder: Int,
    val beloep: Int
)

data class SimulatorVilkaarsproeving(
    val vilkaarErOppfylt: Boolean,
    val alternativ: SimulatorAlternativ?
)

data class SimulatorAlternativ(
    val gradertUttaksalder: SimulatorAlder?,
    val uttaksgrad: Int?,
    val heltUttaksalder: SimulatorAlder
)

data class SimulatorAlder(
    val aar: Int,
    val maaneder: Int
)

data class SimulatorOpptjeningGrunnlag(
    val aar: Int,
    val pensjonsgivendeInntekt: Int
)

data class SimulatorMaanedsbeloep(
    val maanedsbeloepVedGradertUttak: Int?,
    val maanedsbeloepVedHeltUttak: Int
)
