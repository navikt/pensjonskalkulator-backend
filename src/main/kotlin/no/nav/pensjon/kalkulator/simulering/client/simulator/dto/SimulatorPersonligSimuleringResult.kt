package no.nav.pensjon.kalkulator.simulering.client.simulator.dto

// Corresponds to NavSimuleringResultV3 in pensjonssimulator
// (and previously to SimuleringsresultatAlderspensjon1963Plus in PEN)
data class SimulatorPersonligSimuleringResult(
    val alderspensjonListe: List<SimulatorPersonligPensjon>,
    val alderspensjonMaanedsbeloep: SimulatorPersonligMaanedsbeloep?,
    val pre2025OffentligAfp: SimulatorPre2025OffentligAfp?,
    val privatAfpListe: List<SimulatorPersonligPrivatAfp>,
    val livsvarigOffentligAfpListe: List<SimulatorPersonligLivsvarigOffentligAfp>,
    val vilkaarsproeving: SimulatorPersonligVilkaarsproeving?,
    val tilstrekkeligTrygdetidForGarantipensjon: Boolean?,
    val trygdetid: Int?,
    val opptjeningGrunnlagListe: List<SimulatorPersonligOpptjeningGrunnlag>?,
    val error: SimulatorPersonligSimuleringError?
)

data class SimulatorPersonligPensjon(
    val alderAar: Int,
    val beloep: Int,
    val inntektspensjon: Int?,
    val garantipensjon: Int?,
    val delingstall: Double?,
    val pensjonBeholdningFoerUttak: Int?,
    val andelsbroekKap19: Double?,
    val andelsbroekKap20: Double?,
    val sluttpoengtall: Double?,
    val trygdetidKap19: Int?,
    val trygdetidKap20: Int?,
    val poengaarFoer92: Int?,
    val poengaarEtter91: Int?,
    val forholdstall: Double?,
    val grunnpensjon: Int?,
    val tilleggspensjon: Int?,
    val pensjonstillegg: Int?,
    val skjermingstillegg: Int?
)

data class SimulatorPersonligMaanedsbeloep(
    val gradertUttakBeloep: Int?,
    val heltUttakBeloep: Int
)

data class SimulatorPre2025OffentligAfp(
    val alderAar: Int,
    val totaltAfpBeloep: Int,
    val tidligereArbeidsinntekt: Int,
    val grunnbeloep: Int,
    val sluttpoengtall: Double,
    val trygdetid: Int,
    val poengaarTom1991: Int,
    val poengaarFom1992: Int,
    val grunnpensjon: Int,
    val tilleggspensjon: Int,
    val afpTillegg: Int,
    val saertillegg: Int
)

data class SimulatorPersonligPrivatAfp(
    val alderAar: Int,
    val beloep: Int,
    val maanedligBeloep: Int? = null
)

data class SimulatorPersonligLivsvarigOffentligAfp(
    val alderAar: Int,
    val beloep: Int,
    val maanedligBeloep: Int
)

data class SimulatorPersonligVilkaarsproeving(
    val vilkaarErOppfylt: Boolean,
    val alternativ: SimulatorPersonligAlternativ?
)

data class SimulatorPersonligOpptjeningGrunnlag(
    val aar: Int,
    val pensjonsgivendeInntektBeloep: Int
)

data class SimulatorPersonligSimuleringError(
    val exception: String?,
    val message: String
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