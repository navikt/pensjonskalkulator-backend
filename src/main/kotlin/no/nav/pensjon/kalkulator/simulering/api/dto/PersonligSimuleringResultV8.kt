package no.nav.pensjon.kalkulator.simulering.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringResultV8(
    val alderspensjon: List<PersonligSimuleringAlderspensjonResultV8> = emptyList(),
    val alderspensjonMaanedligVedEndring: PersonligSimuleringMaanedligPensjonResultV8? = null,
    val pre2025OffentligAfp: PersonligSimuleringPre2025OffentligAfpResultV8? = null,
    val afpPrivat: List<PersonligSimuleringAarligPensjonResultV8>? = emptyList(),
    val afpOffentlig: List<PersonligSimuleringAarligPensjonResultV8>? = emptyList(),
    val vilkaarsproeving: PersonligSimuleringVilkaarsproevingResultV8,
    val harForLiteTrygdetid: Boolean? = false,
    val trygdetid: Int? = null,
    val opptjeningGrunnlagListe: List<PersonligSimuleringAarligInntektResultV8>? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringMaanedligPensjonResultV8(
    val gradertUttakMaanedligBeloep: Int? = null,
    val heltUttakMaanedligBeloep: Int,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringAlderspensjonResultV8(
    val alder: Int,
    val beloep: Int,
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

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringPre2025OffentligAfpResultV8(
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
    val saertillegg: Int
)

data class PersonligSimuleringAarligPensjonResultV8(
    val alder: Int,
    val beloep: Int,
    val maanedligBeloep: Int?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringVilkaarsproevingResultV8(
    val vilkaarErOppfylt: Boolean,
    val alternativ: PersonligSimuleringAlternativResultV8?
)

data class PersonligSimuleringAarligInntektResultV8(
    val aar: Int,
    val pensjonsgivendeInntektBeloep: Int
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringAlternativResultV8(
    val gradertUttaksalder: PersonligSimuleringAlderResultV8?,
    val uttaksgrad: Int?, // null implies 100 %
    val heltUttaksalder: PersonligSimuleringAlderResultV8
)

data class PersonligSimuleringAlderResultV8(
    val aar: Int,
    val maaneder: Int
)
