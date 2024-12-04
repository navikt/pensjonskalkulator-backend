package no.nav.pensjon.kalkulator.simulering.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PersonligSimuleringResultV8(
    val alderspensjon: List<PersonligSimuleringAlderspensjonResultV8> = emptyList(),
    val alderspensjonMaanedligVedEndring: PersonligSimuleringMaanedligPensjonResultV8? = null,
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
    val pensjonBeholdningFoerUttakBeloep: Int? = null
)

data class PersonligSimuleringAarligPensjonResultV8(
    val alder: Int,
    val beloep: Int
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