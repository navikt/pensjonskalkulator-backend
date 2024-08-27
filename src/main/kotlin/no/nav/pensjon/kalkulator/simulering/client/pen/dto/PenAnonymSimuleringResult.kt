package no.nav.pensjon.kalkulator.simulering.client.pen.dto

// Corresponds to ForenkletSimuleringResultat in PEN
data class PenAnonymSimuleringResult(
    val alderspensjonAndelKapittel19: Double,
    val alderspensjonAndelKapittel20: Double,
    val alderspensjonPerioder: List<PenAnonymPensjonsperiode>,
    val afpPrivatPerioder: List<PenAnonymSimulertAfpPrivatPeriode>,
    val afpOffentligPerioder: List<PenAnonymSimulertAfpOffentligPeriode>,
)

data class PenAnonymPensjonsperiode(
    val belop: Int?,
    val alder: Int?,
    val simulertBeregningsinformasjon: PenAnonymSimulertBeregningsinformasjon?
)

data class PenAnonymSimulertAfpPrivatPeriode(
    val alder: Int?,
    val belopArlig: Int?,
    val belopMnd: Int?
)

data class PenAnonymSimulertAfpOffentligPeriode(
    val alder: Int?,
    val belopArlig: Int?,
    val belopMnd: Int?
)

data class PenAnonymSimulertBeregningsinformasjon(
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
    val garantipensjon: Int?,
)
