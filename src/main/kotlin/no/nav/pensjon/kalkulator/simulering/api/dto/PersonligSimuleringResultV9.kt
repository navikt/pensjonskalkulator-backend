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
    val afpAvkortetTil70Prosent: Boolean
)

data class PersonligSimuleringAfpPrivatResultV9(
    val alder: Int,
    val beloep: Int,
    val kompensasjonstillegg: Int,
    val kronetillegg: Int,
    val livsvarig: Int,
    val maanedligBeloep: Int?
)

data class PersonligSimuleringAarligPensjonResultV9(
    val alder: Int,
    val beloep: Int,
    val maanedligBeloep: Int?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringVilkaarsproevingResultV9(
    val vilkaarErOppfylt: Boolean,
    val alternativ: PersonligSimuleringAlternativResultV9?
)

data class PersonligSimuleringAarligInntektResultV9(
    val aar: Int,
    val pensjonsgivendeInntektBeloep: Int
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringAlternativResultV9(
    val gradertUttaksalder: PersonligSimuleringAlderResultV9?,
    val uttaksgrad: Int?, // null implies 100 %
    val heltUttaksalder: PersonligSimuleringAlderResultV9
)

data class PersonligSimuleringAlderResultV9(
    val aar: Int,
    val maaneder: Int
)
