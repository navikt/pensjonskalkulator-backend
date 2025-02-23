package no.nav.pensjon.kalkulator.simulering.client.simulator.dto

data class SimulatorAnonymSimuleringResultEnvelope(
    val result: SimulatorAnonymSimuleringResult? = null,
    val error: SimulatorAnonymSimuleringError? = null
)

// Corresponds to ForenkletSimuleringResultat in PEN
data class SimulatorAnonymSimuleringResult(
    val alderspensjonAndelKapittel19: Double,
    val alderspensjonAndelKapittel20: Double,
    val alderspensjonPerioder: List<SimulatorAnonymPensjonsperiode>,
    val afpPrivatPerioder: List<SimulatorAnonymPrivatAfpPeriode>,
    val afpOffentligPerioder: List<SimulatorAnonymLivsvarigOffentligAfpPeriode>
)

data class SimulatorAnonymPensjonsperiode(
    val belop: Int?,
    val alder: Int?,
    val simulertBeregningsinformasjon: SimulatorAnonymBeregningInformasjon?
)

data class SimulatorAnonymPrivatAfpPeriode(
    val alder: Int?,
    val belopArlig: Int?,
    val belopMnd: Int?
)

data class SimulatorAnonymLivsvarigOffentligAfpPeriode(
    val alder: Int?,
    val belopArlig: Int?,
    val belopMnd: Int?
)

data class SimulatorAnonymBeregningInformasjon(
    val spt: Double?,
    val gp: Int?,
    val tp: Int?,
    val ttAnvKap19: Int?,
    val ttAnvKap20: Int?,
    val paE91: Int?,
    val paF92: Int?,
    val forholdstall: Double?,
    val delingstall: Double?,
    val pensjonsbeholdningEtterUttak: Int?,
    val inntektspensjon: Int?,
    val garantipensjon: Int?
)

data class SimulatorAnonymSimuleringError(
    val status: String,
    val message: String
)
