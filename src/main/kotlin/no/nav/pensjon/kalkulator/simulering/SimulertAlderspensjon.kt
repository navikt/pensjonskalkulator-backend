package no.nav.pensjon.kalkulator.simulering

data class SimulertAlderspensjon(
    val alder: Int,
    val beloep: Int,
    val inntektspensjonBeloep: Int,
    val garantipensjonBeloep: Int,
    val delingstall: Double,
    val pensjonBeholdningFoerUttak: Int,
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

