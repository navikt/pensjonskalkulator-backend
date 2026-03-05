package no.nav.pensjon.kalkulator.lagring.client.skribenten.dto

data class SimuleringBrevDtoV1(
    val alderspensjonListe: List<AlderspensjonBrevDtoV1>,
    val livsvarigOffentligAfpListe: List<LivsvarigOffentligAfpBrevDtoV1>,
    val tidsbegrensetOffentligAfp: TidsbegrensetOffentligAfpBrevDtoV1?,
    val privatAfpListe: List<AfpPrivatBrevDtoV1>,
    val vilkaarsproevingsresultat: VilkaarsproevingsresultatBrevDtoV1,
    val trygdetid: TrygdetidBrevDtoV1?,
    val pensjonsgivendeInntektListe: List<AarligBeloepBrevDtoV1>,
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
