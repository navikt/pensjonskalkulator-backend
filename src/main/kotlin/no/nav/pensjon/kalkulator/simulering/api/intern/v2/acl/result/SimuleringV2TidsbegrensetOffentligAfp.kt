package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.simulering.SimulertPre2025OffentligAfp

data class SimuleringV2TidsbegrensetOffentligAfp(
    @field:Schema(description = "Personens alder (år) som informasjonen gjelder for")
    @field:NotNull
    val alderAar: Int,

    @field:Schema(description = "Totalt årlig AFP-beløp")
    @field:NotNull
    val totaltAfpBeloep: Int,

    @field:Schema(description = "Tidligere arbeidsinntekt")
    @field:NotNull
    val tidligereArbeidsinntekt: Int,

    @field:Schema(description = "Grunnbeløp")
    @field:NotNull
    val grunnbeloep: Int,

    @field:Schema(description = "Sluttpoengtall")
    @field:NotNull
    val sluttpoengtall: Double,

    @field:Schema(description = "Antall år trygdetid")
    @field:NotNull
    val trygdetid: Int,

    @field:Schema(description = "Antall poengår til og med 1991")
    @field:NotNull
    val poengaarTom1991: Int,

    @field:Schema(description = "Antall poengår fra og med 1992")
    @field:NotNull
    val poengaarFom1992: Int,

    @field:Schema(description = "Grunnpensjon")
    @field:NotNull
    val grunnpensjon: Int,

    @field:Schema(description = "Tilleggspensjon")
    @field:NotNull
    val tilleggspensjon: Int,

    @field:Schema(description = "AFP-tillegg")
    @field:NotNull
    val afpTillegg: Int,

    @field:Schema(description = "Særtillegg")
    @field:NotNull
    val saertillegg: Int,

    @field:Schema(description = "AFP-grad")
    @field:NotNull
    val afpGrad: Int,

    @field:Schema(description = "Hvorvidt AFP-ytelsen er avkortet i.h.t. 70-prosentregelen")
    @field:NotNull
    val erAvkortet: Boolean
) {
    companion object {
        fun transferable(source: SimulertPre2025OffentligAfp) =
            SimuleringV2TidsbegrensetOffentligAfp(
                alderAar = source.alderAar,
                totaltAfpBeloep = source.totaltAfpBeloep,
                tidligereArbeidsinntekt = source.tidligereArbeidsinntekt,
                grunnbeloep = source.grunnbeloep,
                sluttpoengtall = source.sluttpoengtall,
                trygdetid = source.trygdetid,
                poengaarTom1991 = source.poengaarTom1991,
                poengaarFom1992 = source.poengaarFom1992,
                grunnpensjon = source.grunnpensjon,
                tilleggspensjon = source.tilleggspensjon,
                afpTillegg = source.afpTillegg,
                saertillegg = source.saertillegg,
                afpGrad = source.afpGrad,
                erAvkortet = source.afpAvkortetTil70Prosent
            )
    }
}