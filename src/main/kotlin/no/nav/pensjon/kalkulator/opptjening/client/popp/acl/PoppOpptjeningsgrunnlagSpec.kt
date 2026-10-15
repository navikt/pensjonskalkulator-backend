package no.nav.pensjon.kalkulator.opptjening.client.popp.acl

data class PoppOpptjeningsgrunnlagSpec(
    val fnr: String,
    val grunnlagstypeListe: List<PoppOpptjeningsgrunnlagType>? = null,
    val fomAr: Int? = null,
    val tomAr: Int? = null
)

enum class PoppOpptjeningsgrunnlagType {
    DAGPENGER,
    FORST_TJENESTE,
    INNTEKT,
    OMSORG
}