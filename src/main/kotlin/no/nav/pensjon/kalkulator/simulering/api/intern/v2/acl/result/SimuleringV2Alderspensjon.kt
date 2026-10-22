package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.simulering.SimulertAlderspensjon

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimuleringV2Alderspensjon(
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
) {
    companion object {
        fun transferable(source: SimulertAlderspensjon) =
            SimuleringV2Alderspensjon(
                alderAar = source.alder,
                beloep = source.beloep,
                inntektspensjonBeloep = source.inntektspensjonBeloep,
                basispensjonBeloep = source.kapittel19Pensjon?.basispensjon,
                garantipensjonBeloep = source.kapittel20Pensjon?.garantipensjon?.aarligBeloep,
                garantipensjonSats = source.kapittel20Pensjon?.garantipensjon?.sats,
                garantitilleggBeloep = source.kapittel20Pensjon?.garantitillegg,
                restpensjonBeloep = source.kapittel19Pensjon?.restpensjon,
                grunnpensjonBeloep = source.grunnpensjon,
                tilleggspensjonBeloep = source.tilleggspensjon,
                pensjonstillegg = source.pensjonstillegg,
                skjermingstillegg = source.skjermingstillegg,
                gjenlevendetillegg = source.kapittel19Pensjon?.gjenlevendetillegg,
                minstePensjonsnivaaSats = source.kapittel19Pensjon?.minstePensjonsnivaaSats,
                delingstall = source.delingstall,
                forholdstall = source.forholdstall,
                pensjonsbeholdningFoerUttakBeloep = source.pensjonBeholdningFoerUttak,
                kapittel19Andel = source.kapittel19Pensjon?.andelsbroek,
                kapittel20Andel = source.kapittel20Pensjon?.andelsbroek,
                sluttpoengtall = source.sluttpoengtall,
                kapittel19Trygdetid = source.kapittel19Pensjon?.trygdetidAntallAar,
                kapittel20Trygdetid = source.kapittel20Pensjon?.trygdetidAntallAar,
                poengaarTom1991 = source.poengaarFoer92,
                poengaarFom1992 = source.poengaarEtter91
            )
    }
}