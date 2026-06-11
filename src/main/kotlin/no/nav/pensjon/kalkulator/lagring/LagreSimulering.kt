package no.nav.pensjon.kalkulator.lagring

import java.time.LocalDate

data class LagreSimulering(
    val alderspensjonListe: List<LagreAlderspensjon>,
    val afpPrivat: LagreAfpPrivatSimulering?,
    val afpOffentligLivsvarig: LagreAfpOffentligLivsvarigSimulering?,
    val afpOffentligTidsbegrenset: LagreTidsbegrensetOffentligAfp?,
    val vilkaarsproevingsresultat: LagreVilkaarsproevingsresultat?,
    val trygdetid: LagreTrygdetid?,
    val pensjonsgivendeInntektListe: List<LagreAarligBeloep>?,
    val aarligInntektOgPensjonListe: List<LagreAarligInntektOgPensjon>?,
    val simuleringsinformasjon: LagreSimuleringsinformasjon?,
    val maanedligAlderspensjonForKnekkpunkter: LagreMaanedligAlderspensjonForKnekkpunkter?,
    val enhetsId: String,
)

data class LagreAlderspensjon(
    val alderAar: Int,
    val beloep: Int,
    val gjenlevendetillegg: Int?
)

data class LagreAfpPrivatSimulering(
    val vedGradertUttak: LagreAfpPrivat?,
    val vedHeltUttak: LagreAfpPrivat,
    val vedNormertPensjonsalder: LagreAfpPrivat?,
)

data class LagreAfpOffentligLivsvarigSimulering(
    val vedGradertUttak: LagreLivsvarigOffentligAfp?,
    val vedHeltUttak: LagreLivsvarigOffentligAfp,
)

data class LagreLivsvarigOffentligAfp(
    val alderAar: Int,
    val aarligBeloep: Int,
    val maanedligBeloep: Int
)

data class LagreTidsbegrensetOffentligAfp(
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

data class LagreAfpPrivat(
    val alderAar: Int,
    val aarligBeloep: Int,
    val kompensasjonstillegg: Int,
    val kronetillegg: Int?,
    val livsvarig: Int,
    val maanedligBeloep: Int
)

data class LagreVilkaarsproevingsresultat(
    val erInnvilget: Boolean,
    val alternativ: LagreUttaksparametre?
)

data class LagreTrygdetid(
    val antallAar: Int,
    val erUtilstrekkelig: Boolean
)

data class LagreAarligBeloep(
    val aarstall: Int,
    val beloep: Int
)

data class LagreUttaksparametre(
    val gradertUttakAlder: LagreAlder?,
    val uttaksgrad: Int?,
    val heltUttakAlder: LagreAlder
)

data class LagreAlder(
    val aar: Int,
    val maaneder: Int
)

data class LagreSimuleringsinformasjon(
    val gradertUttakInformasjon: LagreUttaksinformasjon?,
    val heltUttakInformasjon: LagreUttaksinformasjon,
    val normertUttakInformasjon: LagreUttaksinformasjon?,
    val sivilstatus: String?,
    val utenlandsperioder: List<LagreUtenlandsperiode>?,
    val kull: Kull,
    val normertPensjonsalderPlassering: NormertPensjonsalderPlassering?
)

data class LagreUttaksinformasjon(
    val alder: LagreAlder,
    val uttaksdato: String
)

data class LagreUtenlandsperiode(
    val fom: LocalDate,
    val tom: LocalDate?,
    val landkode: String,
    val arbeidetUtenlands: Boolean?
)

data class LagreMaanedligAlderspensjonForKnekkpunkter(
    val vedGradertUttak: LagreMaanedligAlderspensjon?,
    val vedHeltUttak: LagreMaanedligAlderspensjon,
    val vedNormertPensjonsalder: LagreMaanedligAlderspensjon?
)

data class LagreMaanedligAlderspensjon(
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

data class LagreAarligInntektOgPensjon(
    val alderLabel: String,
    val alderspensjon: Int,
    val avtalefestetPensjon: Int,
    val pensjonsgivendeInntekt: Int,
)
