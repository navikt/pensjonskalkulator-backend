package no.nav.pensjon.kalkulator.simulering.client.simulator.acl.result

import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.simulering.SimulertTidsbegrensetOffentligAfp

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
    @field:NotNull val erAvkortet: Boolean,
    val fremtidigePensjonspoeng: Double? = null
) {
    fun toInternalValue() =
        SimulertTidsbegrensetOffentligAfp(
            alderAar = alderAar,
            totaltAfpBeloep = totaltAfpBeloep,
            tidligereArbeidsinntekt = tidligereArbeidsinntekt,
            grunnbeloep = grunnbeloep,
            sluttpoengtall = sluttpoengtall,
            trygdetid = trygdetid,
            poengaarTom1991 = poengaarTom1991,
            poengaarFom1992 = poengaarFom1992,
            grunnpensjon = grunnpensjon,
            tilleggspensjon = tilleggspensjon,
            afpTillegg = afpTillegg,
            saertillegg = saertillegg,
            afpGrad = afpGrad,
            erAvkortet = erAvkortet,
            fremtidigePensjonspoeng = fremtidigePensjonspoeng ?: 0.0
        )
}