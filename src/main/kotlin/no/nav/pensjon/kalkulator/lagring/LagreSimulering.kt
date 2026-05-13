package no.nav.pensjon.kalkulator.lagring

data class LagreSimulering(
    val alderspensjonListe: List<LagreAlderspensjon>,
    val livsvarigOffentligAfpListe: List<LagreAfpOffentlig>?,
    val tidsbegrensetOffentligAfp: LagreTidsbegrensetOffentligAfp?,
    val privatAfpListe: List<LagreAfpPrivat>?,
    val vilkaarsproevingsresultat: LagreVilkaarsproevingsresultat?,
    val trygdetid: LagreTrygdetid?,
    val pensjonsgivendeInntektListe: List<LagreAarligBeloep>?,
    val simuleringsinformasjon: LagreSimuleringsinformasjon?,
    val enhetsId: String,
)

data class LagreAlderspensjon(
    val alderAar: Int,
    val beloep: Int,
    val gjenlevendetillegg: Int?
)

data class LagreAfpOffentlig(
    val alderAar: Int,
    val aarligBeloep: Int,
    val maanedligBeloep: Int?
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
    val kronetillegg: Int,
    val livsvarig: Int,
    val maanedligBeloep: Int?
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
    val gradertUttaksalder: LagreAlder?,
    val heltUttaksalder: LagreAlder?,
    val maanedligAlderspensjonForKnekkpunkter: LagreMaanedligAlderspensjonForKnekkpunkter?
)

data class LagreMaanedligAlderspensjonForKnekkpunkter(
    val vedGradertUttak: LagreMaanedligAlderspensjon?,
    val vedHeltUttak: LagreMaanedligAlderspensjon,
    val vedNormertPensjonsalder: LagreMaanedligAlderspensjon
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
