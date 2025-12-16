package no.nav.pensjon.kalkulator.simulering.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringResultV9(
    @field:NotNull val alderspensjon: List<PersonligSimuleringAlderspensjonResultV9> = emptyList(),
    val alderspensjonMaanedligVedEndring: PersonligSimuleringMaanedligPensjonResultV9? = null,
    val pre2025OffentligAfp: PersonligSimuleringPre2025OffentligAfpResultV9? = null,
    val afpPrivat: List<PersonligSimuleringAfpPrivatResultV9>? = emptyList(),
    val afpOffentlig: List<PersonligSimuleringAarligPensjonResultV9>? = emptyList(),
    @field:NotNull val vilkaarsproeving: PersonligSimuleringVilkaarsproevingResultV9,
    val harForLiteTrygdetid: Boolean? = false,
    val trygdetid: Int? = null,
    val opptjeningGrunnlagListe: List<PersonligSimuleringAarligInntektResultV9>? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringMaanedligPensjonResultV9(
    val gradertUttakMaanedligBeloep: Int? = null,
    @field:NotNull val heltUttakMaanedligBeloep: Int,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringAlderspensjonResultV9(
    @field:NotNull val alder: Int,
    @field:NotNull val beloep: Int,
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
    val skjermingstillegg: Int? = null,
    val kapittel19Gjenlevendetillegg: Int? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringPre2025OffentligAfpResultV9(
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
    @field:NotNull val afpAvkortetTil70Prosent: Boolean
)

data class PersonligSimuleringAfpPrivatResultV9(
    @field:NotNull val alder: Int,
    @field:NotNull val beloep: Int,
    @field:NotNull val kompensasjonstillegg: Int,
    @field:NotNull val kronetillegg: Int,
    @field:NotNull val livsvarig: Int,
    val maanedligBeloep: Int?
)

data class PersonligSimuleringAarligPensjonResultV9(
    @field:NotNull val alder: Int,
    @field:NotNull val beloep: Int,
    @field:NotNull val maanedligBeloep: Int?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringVilkaarsproevingResultV9(
    @field:NotNull val vilkaarErOppfylt: Boolean,
    val alternativ: PersonligSimuleringAlternativResultV9?
)

data class PersonligSimuleringAarligInntektResultV9(
    @field:NotNull val aar: Int,
    @field:NotNull val pensjonsgivendeInntektBeloep: Int
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringAlternativResultV9(
    val gradertUttaksalder: PersonligSimuleringAlderResultV9?,
    val uttaksgrad: Int?, // null implies 100 %
    @field:NotNull val heltUttaksalder: PersonligSimuleringAlderResultV9
)

data class PersonligSimuleringAlderResultV9(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
)
