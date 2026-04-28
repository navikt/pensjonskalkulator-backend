package no.nav.pensjon.kalkulator.simulering.api.v1.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.validity.ProblemType
import org.springframework.http.HttpStatus

@JsonInclude(NON_NULL)
data class SimuleringV1Result(
    @field:Schema(description = "Alderspensjon for hvert år")
    @field:NotNull
    val alderspensjonListe: List<SimuleringV1Alderspensjon>,

    @field:Schema(description = "Månedlig alderspensjon ved endring av uttaksgrad")
    val maanedligAlderspensjonVedUttaksendring: SimuleringV1Uttaksbeloep?,

    @field:Schema(description = "Månedlig alderspensjon for knekkpunkter")
    val maanedligAlderspensjonForKnekkpunkter: SimuleringV1MaanedligAlderspensjonForKnekkpunkter?,

    @field:Schema(description = "Livsvarig AFP i offentlig sektor for hvert år")
    val livsvarigOffentligAfpListe: List<SimuleringV1AldersbestemtUtbetaling>?,

    @field:Schema(description = "Tidsbegrenset AFP i offentlig sektor ('gammel ordning') for hvert år")
    val tidsbegrensetOffentligAfp: SimuleringV1TidsbegrensetOffentligAfp?,

    @field:Schema(description = "AFP i privat sektor for hvert år")
    val privatAfpListe: List<SimuleringV1PrivatAfp>?,

    @field:Schema(description = "Resultatet av vilkårsprøvingen")
    @field:NotNull
    val vilkaarsproevingsresultat: SimuleringV1Vilkaarsproevingsresultat,

    @field:Schema(description = "Personens trygdetid")
    val trygdetid: SimuleringV1Trygdetid?,

    @field:Schema(description = "Pensjonsgivende inntekter for hvert år")
    val pensjonsgivendeInntektListe: List<SimuleringV1AarligBeloep>?,

    @field:Schema(description = "Eventuelt problem som oppstod under simuleringen")
    val problem: SimuleringV1Problem?,

    @field:Schema(description = "Resultat av serviceberegnet AFP (kun for simuleringstype AFP_FOR_FPP)")
    val serviceberegnetAfp: SimuleringV1ServiceberegnetAfp? = null
)

@JsonInclude(NON_NULL)
data class SimuleringV1Alderspensjon(
    @field:Schema(description = "Personens alder (år) som informasjonen gjelder for")
    @field:NotNull
    val alderAar: Int,

    @field:Schema(description = "Årlig pensjonsbeløp")
    @field:NotNull
    val beloep: Int,

    @field:Schema(description = "Årlig beløp for inntektspensjon; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val inntektspensjonBeloep: Int?,

    @field:Schema(description = "Årlig beløp for basispensjon; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val basispensjonBeloep: Int?,

    @field:Schema(description = "Årlig beløp for garantipensjon; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val garantipensjonBeloep: Int?,

    @field:Schema(description = "Sats for garantipensjon; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val garantipensjonSats: Double?,

    @field:Schema(description = "Årlig beløp for garantitillegg; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val garantitilleggBeloep: Int?,

    @field:Schema(description = "Årlig beløp for restpensjon; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val restpensjonBeloep: Int?,

    @field:Schema(description = "Grunnpensjon; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val grunnpensjonBeloep: Int? = null,

    @field:Schema(description = "Tilleggspensjon; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val tilleggspensjonBeloep: Int? = null,

    @field:Schema(description = "Pensjonstillegg; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val pensjonstillegg: Int? = null,

    @field:Schema(description = "Skjermingstillegg; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val skjermingstillegg: Int? = null,

    @field:Schema(description = "Årlig beløp for gjenlevendetillegg; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val gjenlevendetillegg: Int?,

    @field:Schema(description = "Minste pensjonsnivå-sats; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val minstePensjonsnivaaSats: Double?,

    @field:Schema(description = "Delingstall; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val delingstall: Double?,

    @field:Schema(description = "Forholdstall; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val forholdstall: Double?,

    @field:Schema(description = "Beløp for pensjonsbeholdning før uttak; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val pensjonsbeholdningFoerUttakBeloep: Int?,

    @field:Schema(description = "Andel (0..1) av pensjonen beregnet i.h.t. kapittel 19; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val kapittel19Andel: Double?,

    @field:Schema(description = "Andel (0..1) av pensjonen beregnet i.h.t. kapittel 20; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val kapittel20Andel: Double?,

    @field:Schema(description = "Sluttpoengtall; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val sluttpoengtall: Double?,

    @field:Schema(description = "Antall år trygdetid i.h.t. kapittel 19; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val kapittel19Trygdetid: Int?,

    @field:Schema(description = "Antall år trygdetid i.h.t. kapittel 20; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val kapittel20Trygdetid: Int?,

    @field:Schema(description = "Antall poengår til og med 1991; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val poengaarTom1991: Int?,

    @field:Schema(description = "Antall poengår fra og med 1992; feltet er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val poengaarFom1992: Int?
)

@JsonInclude(NON_NULL)
data class SimuleringV1MaanedligAlderspensjonForKnekkpunkter(
    val vedGradertUttak: SimuleringV1MaanedligAlderspensjon?,
    @field:NotNull val vedHeltUttak: SimuleringV1MaanedligAlderspensjon,
    @field:NotNull val vedNormertPensjonsalder: SimuleringV1MaanedligAlderspensjon
)

@JsonInclude(NON_NULL)
data class SimuleringV1MaanedligAlderspensjon(
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

@JsonInclude(NON_NULL)
data class SimuleringV1Uttaksbeloep(
    @field:Schema(description = "Månedlig pensjonsbeløp ved start av gradert uttak")
    val gradertUttakMaanedligBeloep: Int? = null,

    @field:Schema(description = "Månedlig pensjonsbeløp ved start av helt uttak")
    @field:NotNull
    val heltUttakMaanedligBeloep: Int,
)

@JsonInclude(NON_NULL)
data class SimuleringV1AldersbestemtUtbetaling(
    @field:Schema(description = "Personens alder (år) som informasjonen gjelder for")
    @field:NotNull
    val alderAar: Int,

    @field:Schema(description = "Årlig beløp")
    @field:NotNull val aarligBeloep: Int,
    val maanedligBeloep: Int?
)

data class SimuleringV1TidsbegrensetOffentligAfp(
    @field:Schema(description = "Personens alder (år) som informasjonen gjelder for")
    @field:NotNull
    val alderAar: Int,

    @field:Schema(description = "Totalt årlig AFP-beløp")
    @field:NotNull val totaltAfpBeloep: Int,

    @field:Schema(description = "Tidligere arbeidsinntekt")
    @field:NotNull val tidligereArbeidsinntekt: Int,

    @field:Schema(description = "Grunnbeløp")
    @field:NotNull val grunnbeloep: Int,

    @field:Schema(description = "Sluttpoengtall")
    @field:NotNull val sluttpoengtall: Double,

    @field:Schema(description = "Antall år trygdetid")
    @field:NotNull val trygdetid: Int,

    @field:Schema(description = "Antall poengår til og med 1991")
    @field:NotNull val poengaarTom1991: Int,

    @field:Schema(description = "Antall poengår fra og med 1992")
    @field:NotNull val poengaarFom1992: Int,

    @field:Schema(description = "Grunnpensjon")
    @field:NotNull val grunnpensjon: Int,

    @field:Schema(description = "Tilleggspensjon")
    @field:NotNull val tilleggspensjon: Int,

    @field:Schema(description = "AFP-tillegg")
    @field:NotNull val afpTillegg: Int,

    @field:Schema(description = "Særtillegg")
    @field:NotNull val saertillegg: Int,

    @field:Schema(description = "AFP-grad")
    @field:NotNull val afpGrad: Int,

    @field:Schema(description = "Hvorvidt AFP-ytelsen er avkortet i.h.t. 70-prosentregelen")
    @field:NotNull val erAvkortet: Boolean
)

@JsonInclude(NON_NULL)
data class SimuleringV1PrivatAfp(
    @field:Schema(description = "Personens alder (år) som informasjonen gjelder for")
    @field:NotNull
    val alderAar: Int,

    @field:NotNull val aarligBeloep: Int,
    @field:NotNull val kompensasjonstillegg: Int,
    @field:NotNull val kronetillegg: Int,
    @field:NotNull val livsvarig: Int,
    val maanedligBeloep: Int?
)

@JsonInclude(NON_NULL)
data class SimuleringV1Vilkaarsproevingsresultat(
    @field:NotNull val erInnvilget: Boolean,
    val alternativ: SimuleringV1Uttaksparametre?
)

data class SimuleringV1AarligBeloep(
    @field:Schema(description = "Hvilket årstall (kalenderår) som informasjonen gjelder for")
    @field:NotNull
    val aarstall: Int,

    @field:NotNull val beloep: Int
)

data class SimuleringV1Trygdetid(
    @field:NotNull val antallAar: Int,
    @field:NotNull val erUtilstrekkelig: Boolean
)

@JsonInclude(NON_NULL)
data class SimuleringV1Uttaksparametre(
    val gradertUttakAlder: SimuleringV1Alder?,
    val uttaksgrad: Int?, // null implies 100 %
    @field:NotNull val heltUttakAlder: SimuleringV1Alder
)

data class SimuleringV1Alder(
    @field:Schema(description = "Antall helt fylte år")
    @field:NotNull
    val aar: Int,

    @field:Schema(description = "Antall helt fylte måneder (0..11)")
    @field:NotNull
    val maaneder: Int
)

data class SimuleringV1Problem(
    @field:NotNull val kode: SimuleringV1ProblemType,
    @field:NotNull val beskrivelse: String
)

enum class SimuleringV1ProblemType(
    val internalValue: ProblemType,
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
) {
    UGYLDIG_UTTAKSDATO(internalValue = ProblemType.UGYLDIG_UTTAKSDATO),
    UGYLDIG_UTTAKSGRAD(internalValue = ProblemType.UGYLDIG_UTTAKSGRAD),
    UGYLDIG_SIVILSTATUS(internalValue = ProblemType.UGYLDIG_SIVILSTATUS),
    UGYLDIG_INNTEKT(internalValue = ProblemType.UGYLDIG_INNTEKT),
    UGYLDIG_ANTALL_AAR(internalValue = ProblemType.UGYLDIG_ANTALL_AAR),
    UGYLDIG_PERSONIDENT(internalValue = ProblemType.UGYLDIG_PERSONIDENT),
    PERSON_IKKE_FUNNET(internalValue = ProblemType.PERSON_IKKE_FUNNET, httpStatus = HttpStatus.NOT_FOUND),
    PERSON_FOR_LAV_ALDER(internalValue = ProblemType.PERSON_FOR_LAV_ALDER),
    PERSON_FOR_HOEY_ALDER(internalValue = ProblemType.PERSON_FOR_HOEY_ALDER),
    UTILSTREKKELIG_INNTEKT(internalValue = ProblemType.UTILSTREKKELIG_INNTEKT, httpStatus = HttpStatus.OK),
    UTILSTREKKELIG_OPPTJENING(internalValue = ProblemType.UTILSTREKKELIG_OPPTJENING, httpStatus = HttpStatus.OK),
    UTILSTREKKELIG_TRYGDETID(internalValue = ProblemType.UTILSTREKKELIG_TRYGDETID, httpStatus = HttpStatus.OK),
    ANNEN_KLIENTFEIL(internalValue = ProblemType.ANNEN_KLIENTFEIL),
    INTERN_DATA_INKONSISTENS(internalValue = ProblemType.INTERN_DATA_INKONSISTENS, httpStatus = HttpStatus.INTERNAL_SERVER_ERROR),
    IMPLEMENTASJONSFEIL(internalValue = ProblemType.IMPLEMENTASJONSFEIL, httpStatus = HttpStatus.INTERNAL_SERVER_ERROR),
    TREDJEPARTSFEIL(internalValue = ProblemType.TREDJEPARTSFEIL, httpStatus = HttpStatus.INTERNAL_SERVER_ERROR),
    SERVERFEIL(internalValue = ProblemType.ANNEN_SERVERFEIL, httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
}

@JsonInclude(NON_NULL)
data class SimuleringV1ServiceberegnetAfp(
    val beregnetAfp: SimuleringV1BeregnetAfp?,
)

@JsonInclude(NON_NULL)
data class SimuleringV1BeregnetAfp(
    val totalbelopAfp: Int?,
    val virkFom: java.time.LocalDate?,
    val tidligereArbeidsinntekt: Int?,
    val grunnbelop: Int?,
    val sluttpoengtall: Double?,
    val trygdetid: Int?,
    val poengar: Int?,
    val poeangarF92: Int?,
    val poeangarE91: Int?,
    val grunnpensjon: Int?,
    val tilleggspensjon: Int?,
    val afpTillegg: Int?,
    val fpp: Double?,
    val sertillegg: Int?
)