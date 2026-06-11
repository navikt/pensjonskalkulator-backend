package no.nav.pensjon.kalkulator.lagring.client.skribenten.dto

import java.time.LocalDate

data class SimuleringBrevDtoV1(
    val simulering: SimuleringBrevV1,
    val simuleringsinformasjon: SimuleringsinformasjonBrevDtoV1?,
    val vilkaarsproevingsresultat: VilkaarsproevingsresultatBrevDtoV1?,
    val trygdetid: TrygdetidBrevDtoV1?,
    val pensjonsgivendeInntektListe: List<AarligBeloepBrevDtoV1>?,
    val aarligInntektOgPensjonListe: List<AarligInntektOgPensjonBrevDtoV1>?,
    val forbehold: ForbeholdBrevDtoV1?,
)

data class SimuleringBrevV1(
    val alderspensjonListe: List<AlderspensjonBrevDtoV1>,
    val maanedligAlderspensjonForKnekkpunkter: MaanedligAlderspensjonForKnekkpunkterBrevDtoV1?,
    val afpPrivat: AfpPrivatSimuleringBrevDtoV1?,
    val afpOffentligLivsvarig: AfpOffentligLivsvarigSimuleringBrevDtoV1?,
    val afpOffentligTidsbegrenset: TidsbegrensetOffentligAfpBrevDtoV1?,
)

data class AfpPrivatSimuleringBrevDtoV1(
    val vedGradertUttak: AfpPrivatBrevDtoV1?,
    val vedHeltUttak: AfpPrivatBrevDtoV1,
    val vedNormertPensjonsalder: AfpPrivatBrevDtoV1?,
)

data class AfpOffentligLivsvarigSimuleringBrevDtoV1(
    val vedGradertUttak: LivsvarigOffentligAfpBrevDtoV1?,
    val vedHeltUttak: LivsvarigOffentligAfpBrevDtoV1,
)

data class AlderspensjonBrevDtoV1(
    val alderAar: Int,
    val beloep: Int,
    val gjenlevendetillegg: Int?
)

data class LivsvarigOffentligAfpBrevDtoV1(
    val alderAar: Int,
    val aarligBeloep: Int,
    val maanedligBeloep: Int
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
    val kronetillegg: Int?,
    val livsvarig: Int,
    val maanedligBeloep: Int
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

data class UttaksinformasjonBrevDtoV1(
    val alder: AlderBrevDtoV1,
    val uttaksdato: String
)

data class SimuleringsinformasjonBrevDtoV1(
    val gradertUttakInformasjon: UttaksinformasjonBrevDtoV1?,
    val heltUttakInformasjon: UttaksinformasjonBrevDtoV1,
    val normertUttakInformasjon: UttaksinformasjonBrevDtoV1?,
    val sivilstatus: String?,
    val utenlandsperioder: List<UtenlandsperiodeBrevDtoV1>?,
    val kull: String,
    val normertPensjonsalderPlassering: String?
)

data class UtenlandsperiodeBrevDtoV1(
    val fom: LocalDate,
    val tom: LocalDate?,
    val landkode: String,
    val arbeidetUtenlands: Boolean?
)

data class MaanedligAlderspensjonForKnekkpunkterBrevDtoV1(
    val vedGradertUttak: MaanedligAlderspensjonBrevDtoV1?,
    val vedHeltUttak: MaanedligAlderspensjonBrevDtoV1,
    val vedNormertPensjonsalder: MaanedligAlderspensjonBrevDtoV1?
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
    val kapittel19AndelTeller: Int?,
    val kapittel19Trygdetid: Int?,
    val basispensjonBeloep: Int?,
    val restpensjonBeloep: Int?,
    val gjenlevendetillegg: Int?,
    val minstePensjonsnivaaSats: Double?,
    val minstePensjonsnivaaBeloep: Int?,
    val kapittel20AndelTeller: Int?,
    val kapittel20Trygdetid: Int?,
    val garantipensjonBeloep: Int?,
    val garantipensjonsnivaaBeloep: Int?,
    val garantipensjonSats: Double?,
    val garantitilleggBeloep: Int?,
    val grunnbeloep: Int?
)

data class AarligInntektOgPensjonBrevDtoV1(
    val alderLabel: String,
    val alderspensjon: Int,
    val avtalefestetPensjon: Int,
    val pensjonsgivendeInntekt: Int,
)

data class ForbeholdBrevDtoV1(
    val seksjoner: List<ForbeholdSeksjonBrevDtoV1>?
)

data class ForbeholdSeksjonBrevDtoV1(
    val tittel: String?,
    val avsnitt: List<ForbeholdAvsnittBrevDtoV1>
)

data class ForbeholdAvsnittBrevDtoV1(
    val tekst: String,
    val punktliste: List<String>?
)
