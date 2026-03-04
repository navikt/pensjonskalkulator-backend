package no.nav.pensjon.kalkulator.lagring.api.dto

import jakarta.validation.constraints.NotNull

data class LagreSimuleringSpecDto(
    @field:NotNull val alderspensjonListe: List<LagreAlderspensjonDto>,
    val livsvarigOffentligAfpListe: List<LagreAldersbestemtUtbetalingDto>?,
    val tidsbegrensetOffentligAfp: LagreTidsbegrensetOffentligAfpDto?,
    val privatAfpListe: List<LagrePrivatAfpDto>?,
    @field:NotNull val vilkaarsproevingsresultat: LagreVilkaarsproevingsresultatDto,
    val trygdetid: LagreTrygdetidDto?,
    val pensjonsgivendeInntektListe: List<LagreAarligBeloepDto>?
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
