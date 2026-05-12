package no.nav.pensjon.kalkulator.lagring.client.skribenten.dto

data class SimuleringBrevDtoV1(
    val alderspensjonListe: List<AlderspensjonBrevDtoV1>,
    val livsvarigOffentligAfpListe: List<LivsvarigOffentligAfpBrevDtoV1>,
    val tidsbegrensetOffentligAfp: TidsbegrensetOffentligAfpBrevDtoV1?,
    val privatAfpListe: List<AfpPrivatBrevDtoV1>,
    val vilkaarsproevingsresultat: VilkaarsproevingsresultatBrevDtoV1,
    val trygdetid: TrygdetidBrevDtoV1?,
    val pensjonsgivendeInntektListe: List<AarligBeloepBrevDtoV1>,
    val simuleringsinformasjon: SimuleringsinformasjonBrevDtoV1?,
)

data class AlderspensjonBrevDtoV1(
    val alderAar: Int,
    val beloep: Int,
    val gjenlevendetillegg: Int?
)

data class LivsvarigOffentligAfpBrevDtoV1(
    val alderAar: Int,
    val aarligBeloep: Int,
    val maanedligBeloep: Int?
)

data class TidsbegrensetOffentligAfpBrevDtoV1(
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
    val saertillegg: Int,
    val afpGrad: Int,
    val erAvkortet: Boolean
)

data class AfpPrivatBrevDtoV1(
    val alderAar: Int,
    val aarligBeloep: Int,
    val kompensasjonstillegg: Int,
    val kronetillegg: Int,
    val livsvarig: Int,
    val maanedligBeloep: Int?
)

data class VilkaarsproevingsresultatBrevDtoV1(
    val erInnvilget: Boolean,
    val alternativ: AlternativUttaksparametreBrevDtoV1?
)

data class TrygdetidBrevDtoV1(
    val antallAar: Int,
    val erUtilstrekkelig: Boolean
)

data class AarligBeloepBrevDtoV1(
    val aarstall: Int,
    val beloep: Int
)

data class AlternativUttaksparametreBrevDtoV1(
    val gradertUttakAlder: AlderBrevDtoV1?,
    val uttaksgrad: Int?,
    val heltUttakAlder: AlderBrevDtoV1
)

data class AlderBrevDtoV1(
    val aar: Int,
    val maaneder: Int
)

data class SimuleringsinformasjonBrevDtoV1(
    val gradertUttaksalder: AlderBrevDtoV1?,
    val heltUttaksalder: AlderBrevDtoV1?,
    val maanedligAlderspensjonForKnekkpunkter: MaanedligAlderspensjonForKnekkpunkterBrevDtoV1?
)

data class MaanedligAlderspensjonForKnekkpunkterBrevDtoV1(
    val vedGradertUttak: MaanedligAlderspensjonBrevDtoV1?,
    val vedHeltUttak: MaanedligAlderspensjonBrevDtoV1,
    val vedNormertPensjonsalder: MaanedligAlderspensjonBrevDtoV1
)

data class MaanedligAlderspensjonBrevDtoV1(
    val beloep: Int,
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
