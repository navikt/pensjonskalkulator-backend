package no.nav.pensjon.kalkulator.simulering.api.v1.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.validity.ProblemType
import org.springframework.http.HttpStatus

@JsonInclude(NON_NULL)
data class SimuleringV1Result(
    @field:NotNull val alderspensjonListe: List<SimuleringV1Alderspensjon>,
    val livsvarigOffentligAfpListe: List<SimuleringV1AldersbestemtUtbetaling>?,
    val tidsbegrensetOffentligAfp: SimuleringV1TidsbegrensetOffentligAfp?,
    val privatAfpListe: List<SimuleringV1PrivatAfp>?,

    @field:Schema(description = "Resultatet av vilkårsprøvingen")
    @field:NotNull
    val vilkaarsproevingsresultat: SimuleringV1Vilkaarsproevingsresultat,

    val trygdetid: SimuleringV1Trygdetid?,
    val pensjonsgivendeInntektListe: List<SimuleringV1AarligBeloep>?,
    val problem: SimuleringV1Problem?
)

@JsonInclude(NON_NULL)
data class SimuleringV1Alderspensjon(
    @field:Schema(description = "Personens alder (år) som informasjonen gjelder for")
    @field:NotNull
    val alderAar: Int,

    @field:Schema(description = "Årlig pensjonsbeløp")
    @field:NotNull
    val beloep: Int,

    @field:Schema(description = "Informasjon om gjenlevendetillegg er ikke inkludert i eksternvariant når funksjonsbryter 'utvidet-simuleringsresultat' er 'av'")
    val gjenlevendetillegg: Int?,

    @field:Schema(description = "Utvidet informasjon om alderspensjonen; kun inkludert i eksternvariant og kun når funksjonsbryter 'utvidet-simuleringsresultat' er 'på'")
    val extension: SimuleringV1AlderspensjonExtension?
)

@JsonInclude(NON_NULL)
data class SimuleringV1AlderspensjonExtension(
    val inntektspensjonBeloep: Int? = null,
    val garantipensjonBeloep: Int? = null,
    val delingstall: Double? = null,
    val pensjonBeholdningFoerUttakBeloep: Int? = null,
    val andelsbroekKap19: Double? = null,
    val andelsbroekKap20: Double? = null,
    val sluttpoengtall: Double? = null,
    val trygdetidKap19: Int? = null,
    val trygdetidKap20: Int? = null,
    val poengaarFoer92: Int? = null,
    val poengaarEtter91: Int? = null,
    val forholdstall: Double? = null,
    val grunnpensjon: Int? = null,
    val tilleggspensjon: Int? = null,
    val pensjonstillegg: Int? = null,
    val skjermingstillegg: Int? = null
)

@JsonInclude(NON_NULL)
data class SimuleringV1AldersbestemtUtbetaling(
    @field:Schema(description = "Personens alder (år) som informasjonen gjelder for")
    @field:NotNull
    val alderAar: Int,

    @field:NotNull val aarligBeloep: Int,
    val maanedligBeloep: Int?
)

data class SimuleringV1TidsbegrensetOffentligAfp(
    @field:Schema(description = "Personens alder (år) som informasjonen gjelder for")
    @field:NotNull
    val alderAar: Int,

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
    PERSON_FOR_HOEY_ALDER(internalValue = ProblemType.PERSON_FOR_HOEY_ALDER),
    UTILSTREKKELIG_OPPTJENING(internalValue = ProblemType.UTILSTREKKELIG_OPPTJENING, httpStatus = HttpStatus.OK),
    UTILSTREKKELIG_TRYGDETID(internalValue = ProblemType.UTILSTREKKELIG_TRYGDETID, httpStatus = HttpStatus.OK),
    ANNEN_KLIENTFEIL(internalValue = ProblemType.ANNEN_KLIENTFEIL),
    SERVERFEIL(internalValue = ProblemType.SERVERFEIL, httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
}