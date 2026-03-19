package no.nav.pensjon.kalkulator.simulering

data class SimulertAlderspensjon(
    val alder: Int,
    val beloep: Int,
    val inntektspensjonBeloep: Int,
    val delingstall: Double,
    val pensjonBeholdningFoerUttak: Int,
    val sluttpoengtall: Double?,
    val poengaarFoer92: Int?,
    val poengaarEtter91: Int?,
    val forholdstall: Double?,
    val grunnpensjon: Int?,
    val tilleggspensjon: Int?,
    val pensjonstillegg: Int?,
    val skjermingstillegg: Int?,
    val kapittel19Pensjon: Kapittel19Pensjon?,
    val kapittel20Pensjon: Kapittel20Pensjon?
)

data class Kapittel19Pensjon(
    val andelsbroek: Double?,
    val trygdetidAntallAar: Int?,
    val basispensjon: Int?,
    val restpensjon: Int?,
    val gjenlevendetillegg: Int?,
    val minstePensjonsnivaaSats: Double?
)

data class Kapittel20Pensjon(
    val andelsbroek: Double?,
    val trygdetidAntallAar: Int?,
    val garantipensjon: Garantipensjon?,
    val garantitillegg: Int?
)

data class Garantipensjon(
    val aarligBeloep: Int,
    val sats: Double
)