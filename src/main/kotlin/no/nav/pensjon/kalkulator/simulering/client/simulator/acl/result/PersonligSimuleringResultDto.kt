package no.nav.pensjon.kalkulator.simulering.client.simulator.acl.result

import jakarta.validation.constraints.NotNull

data class PersonligSimuleringResultDto(
    @field:NotNull val alderspensjonListe: List<AlderspensjonDto>,
    val alderspensjonMaanedsbeloep: UttaksbeloepDto?,
    @field:NotNull val livsvarigOffentligAfpListe: List<AldersbestemtUtbetalingDto>,
    val tidsbegrensetOffentligAfp: TidsbegrensetOffentligAfpDto?,
    @field:NotNull val privatAfpListe: List<PrivatAfpDto>,
    val primaerTrygdetid: TrygdetidDto?,
    @field:NotNull val vilkaarsproevingsresultat: VilkaarsproevingsresultatDto,
    @field:NotNull val pensjonsgivendeInntektListe: List<AarligBeloepDto>,
    val problem: ProblemDto? = null
)

data class AlderspensjonDto(
    @field:NotNull val alderAar: Int,
    @field:NotNull val beloep: Int,
    val inntektspensjon: Int?,
    val garantipensjon: Int?,
    val delingstall: Double?,
    val pensjonsbeholdningFoerUttak: Int?,
    val sluttpoengtall: Double?,
    val poengaarFoer92: Int?,
    val poengaarEtter91: Int?,
    val forholdstall: Double?,
    val grunnpensjon: Int?,
    val tilleggspensjon: Int?,
    val pensjonstillegg: Int?,
    val skjermingstillegg: Int?,
    val kapittel19Pensjon: Kapittel19PensjonDto?,
    val kapittel20Pensjon: Kapittel20PensjonDto?
)

data class Kapittel19PensjonDto(
    val andelsbroek: Double?,
    val trygdetidAntallAar: Int?,
    val gjenlevendetillegg: Int?
)

data class Kapittel20PensjonDto(
    val andelsbroek: Double?,
    val trygdetidAntallAar: Int?
)

data class UttaksbeloepDto(
    val gradertUttakBeloep: Int?,
    @field:NotNull val heltUttakBeloep: Int
)

data class AldersbestemtUtbetalingDto(
    @field:NotNull val alderAar: Int,
    @field:NotNull val beloep: Int,
    @field:NotNull val maanedligBeloep: Int
)

data class TidsbegrensetOffentligAfpDto(
    @field:NotNull val alderAar: Int,
    @field:NotNull val totaltAfpBeloep: Int,
    @field:NotNull val tidligereArbeidsinntekt: Int,
    @field:NotNull val grunnbeloep: Int,
    @field:NotNull val sluttpoengtall: Double,
    @field:NotNull val trygdetid: Int,
    @field:NotNull val poengaarTom1991: Int,
    @field:NotNull val poengaarFom1992: Int,
    @field:NotNull val grunnpensjon: Int,
    @field:NotNull val tilleggspensjon: Int,
    @field:NotNull val afpTillegg: Int,
    @field:NotNull val saertillegg: Int,
    @field:NotNull val afpGrad: Int,
    @field:NotNull val erAvkortet: Boolean
)

data class PrivatAfpDto(
    @field:NotNull val alderAar: Int,
    @field:NotNull val beloep: Int,
    @field:NotNull val kompensasjonstillegg: Int,
    @field:NotNull val kronetillegg: Int,
    @field:NotNull val livsvarig: Int,
    @field:NotNull val maanedligBeloep: Int
)

data class VilkaarsproevingsresultatDto(
    @field:NotNull val erInnvilget: Boolean,
    val alternativ: UttaksparametreDto?
)

data class AarligBeloepDto(
    @field:NotNull val aarstall: Int,
    @field:NotNull val beloep: Int
)

/**
 * For 'erUtilstrekkelig' gjelder:
 * - Kapittel 19: Angir om trygdetiden er for kort for alderspensjon
 * - Kapittel 20: Angir om trygdetiden er for kort for garantipensjon
 */
data class TrygdetidDto(
    @field:NotNull val antallAar: Int,
    @field:NotNull val erUtilstrekkelig: Boolean
)

data class UttaksparametreDto(
    val gradertUttakAlder: AlderDto?,
    @field:NotNull val uttaksgrad: String, // UttaksgradDto
    @field:NotNull val heltUttakAlder: AlderDto
)

data class AlderDto(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
)

data class ProblemDto(
    @field:NotNull val kode: String, // ProblemTypeDto
    @field:NotNull val beskrivelse: String
)
