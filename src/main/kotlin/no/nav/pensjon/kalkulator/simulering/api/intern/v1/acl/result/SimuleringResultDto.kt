package no.nav.pensjon.kalkulator.simulering.api.intern.v1.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.validity.ProblemType
import org.springframework.http.HttpStatus

@JsonInclude(NON_NULL)
data class SimuleringResultDto(
    @field:NotNull val alderspensjon: List<AlderspensjonDto>,
    val livsvarigOffentligAfp: List<AldersbestemtUtbetalingDto>?,
    val tidsbegrensetOffentligAfp: TidsbegrensetOffentligAfpDto?,
    val privatAfp: List<PrivatAfpDto>?,
    @field:NotNull val vilkaarsproevingsresultat: VilkaarsproevingsresultatDto,
    val trygdetid: TrygdetidDto?,
    val opptjeningsgrunnlagListe: List<PensjonsgivendeInntektDto>?,
    val problem: ProblemDto?
)

@JsonInclude(NON_NULL)
data class AlderspensjonDto(
    @field:NotNull val alderAar: Int,
    @field:NotNull val beloep: Int,
    val gjenlevendetillegg: Int?
)

@JsonInclude(NON_NULL)
data class MaanedligPensjonDto(
    val gradertUttakBeloep: Int?,
    @field:NotNull val heltUttakBeloep: Int
)

@JsonInclude(NON_NULL)
data class AldersbestemtUtbetalingDto(
    @field:NotNull val alderAar: Int,
    @field:NotNull val aarligBeloep: Int,
    val maanedligBeloep: Int?
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

@JsonInclude(NON_NULL)
data class PrivatAfpDto(
    @field:NotNull val alderAar: Int,
    @field:NotNull val aarligBeloep: Int,
    @field:NotNull val kompensasjonstillegg: Int,
    @field:NotNull val kronetillegg: Int,
    @field:NotNull val livsvarig: Int,
    val maanedligBeloep: Int?
)

@JsonInclude(NON_NULL)
data class VilkaarsproevingsresultatDto(
    @field:NotNull val erInnvilget: Boolean,
    val alternativ: UttaksparametreDto?
)

data class PensjonsgivendeInntektDto(
    @field:NotNull val aarstall: Int,
    @field:NotNull val beloep: Int
)

data class TrygdetidDto(
    @field:NotNull val antallAar: Int,
    @field:NotNull val erUtilstrekkelig: Boolean
)

@JsonInclude(NON_NULL)
data class UttaksparametreDto(
    val gradertUttakAlder: AlderDto?,
    val uttaksgrad: Int?, // null implies 100 %
    @field:NotNull val heltUttakAlder: AlderDto
)

data class AlderDto(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
)

data class ProblemDto(
    @field:NotNull val kode: ProblemTypeDto,
    @field:NotNull val beskrivelse: String
)

enum class ProblemTypeDto(
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