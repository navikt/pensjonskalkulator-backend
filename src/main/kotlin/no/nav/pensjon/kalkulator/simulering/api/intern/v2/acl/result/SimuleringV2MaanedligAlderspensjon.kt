package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.simulering.SimulertMaanedligAlderspensjon

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimuleringV2MaanedligAlderspensjon(
    @field:Schema(description = "Pensjonsbeløp i norske kroner")
    @field:NotNull
    val beloep: Int,

    @field:Schema(description = "Inntektspensjon (beløp i norske kroner)")
    val inntektspensjonBeloep: Int?,

    @field:Schema(description = "Delingstall")
    val delingstall: Double?,

    @field:Schema(description = "Pensjonsbeholdning før uttak (beløp i norske kroner)")
    val pensjonsbeholdningFoerUttakBeloep: Int?,

    @field:Schema(description = "Pensjonsbeholdning ettr uttak (beløp i norske kroner)")
    val pensjonsbeholdningEtterUttakBeloep: Int?,

    @field:Schema(description = "Sluttpoengtall")
    val sluttpoengtall: Double?,

    @field:Schema(description = "Antall poengår til og med 1991")
    val poengaarTom1991: Int?,

    @field:Schema(description = "Antall poengår fra og med 1992")
    val poengaarFom1992: Int?,

    @field:Schema(description = "Forholdstall")
    val forholdstall: Double?,

    @field:Schema(description = "Grunnpensjon (beløp i norske kroner)")
    val grunnpensjonBeloep: Int?,

    @field:Schema(description = "Tilleggspensjon (beløp i norske kroner)")
    val tilleggspensjonBeloep: Int?,

    @field:Schema(description = "Pensjonstillegg (beløp i norske kroner)")
    val pensjonstillegg: Int?,

    @field:Schema(description = "Skjermingstillegg (beløp i norske kroner)")
    val skjermingstillegg: Int?,

    @field:Schema(description = "Andel av pensjonen som er beregnet i henhold til kapittel 19 i Folketrygdloven")
    val kapittel19Andel: Double?,

    @field:Schema(description = "Trygdetid (antall år) beregnet i henhold til kapittel 19 i Folketrygdloven")
    val kapittel19Trygdetid: Int?,

    @field:Schema(description = "Basispensjon (beløp i norske kroner)")
    val basispensjonBeloep: Int?,

    @field:Schema(description = "Restpensjon (beløp i norske kroner)")
    val restpensjonBeloep: Int?,

    @field:Schema(description = "Gjenlevendetillegg (beløp i norske kroner)")
    val gjenlevendetillegg: Int?,

    @field:Schema(description = "Sats for minste pensjonsnivå")
    val minstePensjonsnivaaSats: Double?,

    @field:Schema(description = "Andel av pensjonen som er beregnet i henhold til kapittel 20 i Folketrygdloven")
    val kapittel20Andel: Double?,

    @field:Schema(description = "Trygdetid (antall år) beregnet i henhold til kapittel 20 i Folketrygdloven")
    val kapittel20Trygdetid: Int?,

    @field:Schema(description = "Garantipensjon (beløp i norske kroner)")
    val garantipensjonBeloep: Int?,

    @field:Schema(description = "Sats for garantipensjon")
    val garantipensjonSats: Double?,

    @field:Schema(description = "Garantitillegg (beløp i norske kroner)")
    val garantitilleggBeloep: Int?
) {
    companion object {
        fun transferable(source: SimulertMaanedligAlderspensjon) =
            SimuleringV2MaanedligAlderspensjon(
                beloep = source.beloep,
                inntektspensjonBeloep = source.inntektspensjonBeloep,
                delingstall = source.delingstall,
                pensjonsbeholdningFoerUttakBeloep = source.pensjonsbeholdningFoerUttak,
                pensjonsbeholdningEtterUttakBeloep = source.pensjonsbeholdningEtterUttak,
                sluttpoengtall = source.sluttpoengtall,
                poengaarTom1991 = source.poengaarFoer92,
                poengaarFom1992 = source.poengaarEtter91,
                forholdstall = source.forholdstall,
                grunnpensjonBeloep = source.grunnpensjon,
                tilleggspensjonBeloep = source.tilleggspensjon,
                pensjonstillegg = source.pensjonstillegg,
                skjermingstillegg = source.skjermingstillegg,
                kapittel19Andel = source.kapittel19Pensjon?.andelsbroek,
                kapittel19Trygdetid = source.kapittel19Pensjon?.trygdetidAntallAar,
                basispensjonBeloep = source.kapittel19Pensjon?.basispensjon,
                restpensjonBeloep = source.kapittel19Pensjon?.restpensjon,
                gjenlevendetillegg = source.kapittel19Pensjon?.gjenlevendetillegg,
                minstePensjonsnivaaSats = source.kapittel19Pensjon?.minstePensjonsnivaaSats,
                kapittel20Andel = source.kapittel20Pensjon?.andelsbroek,
                kapittel20Trygdetid = source.kapittel20Pensjon?.trygdetidAntallAar,
                garantipensjonBeloep = source.kapittel20Pensjon?.garantipensjon?.maanedligBeloep,
                garantipensjonSats = source.kapittel20Pensjon?.garantipensjon?.sats,
                garantitilleggBeloep = source.kapittel20Pensjon?.garantitillegg
            )
    }
}