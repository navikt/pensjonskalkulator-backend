package no.nav.pensjon.kalkulator.lagring.api.dto

import jakarta.validation.constraints.NotNull

data class LagreSimuleringSpecDtoV1(
    @field:NotNull val alderspensjonListe: List<LagreAlderspensjonDto>,
    val livsvarigOffentligAfpListe: List<LagreAldersbestemtUtbetalingDto>?,
    val tidsbegrensetOffentligAfp: LagreTidsbegrensetOffentligAfpDto?,
    val privatAfpListe: List<LagrePrivatAfpDto>?,
    @field:NotNull val vilkaarsproevingsresultat: LagreVilkaarsproevingsresultatDto,
    val trygdetid: LagreTrygdetidDto?,
    val pensjonsgivendeInntektListe: List<LagreAarligBeloepDto>?,
    val simuleringsinformasjon: LagreSimuleringsinformasjonDto?,
    val navEnhetId: String?,
)

data class LagreAlderspensjonDto(
    @field:NotNull val alderAar: Int,
    @field:NotNull val beloep: Int,
    val gjenlevendetillegg: Int?
)

data class LagreAldersbestemtUtbetalingDto(
    @field:NotNull val alderAar: Int,
    @field:NotNull val aarligBeloep: Int,
    val maanedligBeloep: Int?
)

data class LagreTidsbegrensetOffentligAfpDto(
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

data class LagrePrivatAfpDto(
    @field:NotNull val alderAar: Int,
    @field:NotNull val aarligBeloep: Int,
    @field:NotNull val kompensasjonstillegg: Int,
    @field:NotNull val kronetillegg: Int,
    @field:NotNull val livsvarig: Int,
    val maanedligBeloep: Int?
)

data class LagreVilkaarsproevingsresultatDto(
    @field:NotNull val erInnvilget: Boolean,
    val alternativ: LagreUttaksparametreDto?
)

data class LagreTrygdetidDto(
    @field:NotNull val antallAar: Int,
    @field:NotNull val erUtilstrekkelig: Boolean
)

data class LagreAarligBeloepDto(
    @field:NotNull val aarstall: Int,
    @field:NotNull val beloep: Int
)

data class LagreUttaksparametreDto(
    val gradertUttakAlder: LagreAlderDto?,
    val uttaksgrad: Int?,
    @field:NotNull val heltUttakAlder: LagreAlderDto
)

data class LagreAlderDto(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
)

data class LagreSimuleringsinformasjonDto(
    val gradertUttaksalder: LagreAlderDto?,
    val heltUttaksalder: LagreAlderDto?,
    val maanedligAlderspensjonForKnekkpunkter: LagreMaanedligAlderspensjonForKnekkpunkterDto?
)

data class LagreMaanedligAlderspensjonForKnekkpunkterDto(
    val vedGradertUttak: LagreMaanedligAlderspensjonDto?,
    @field:NotNull val vedHeltUttak: LagreMaanedligAlderspensjonDto,
    @field:NotNull val vedNormertPensjonsalder: LagreMaanedligAlderspensjonDto
)

data class LagreMaanedligAlderspensjonDto(
    @field:NotNull val beloep: Int,
    val inntektspensjonBeloep: Int?,
    val delingstall: Double?,
    val pensjonsbeholdningFoerUttakBeloep: Int?,
    val pensjonsbeholdningEtterUttakBeloep: Int?,
    val sluttpoengtall: Double?,
    val poengaarTom1991: Int?,
    val poengaarFom1992: Int?,
    val forholdstall: Double?,
    val grunnpensjonBeloep: Int?,
    val tilleggspensjonBeloep: Int?,
    val pensjonstillegg: Int?,
    val skjermingstillegg: Int?,
    val kapittel19Andel: Double?,
    val kapittel19Trygdetid: Int?,
    val basispensjonBeloep: Int?,
    val restpensjonBeloep: Int?,
    val gjenlevendetillegg: Int?,
    val minstePensjonsnivaaSats: Double?,
    val kapittel20Andel: Double?,
    val kapittel20Trygdetid: Int?,
    val garantipensjonBeloep: Int?,
    val garantipensjonSats: Double?,
    val garantitilleggBeloep: Int?
)
