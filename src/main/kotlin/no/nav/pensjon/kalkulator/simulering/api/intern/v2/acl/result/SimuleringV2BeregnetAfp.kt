package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import no.nav.pensjon.kalkulator.afp.BeregnetAfp
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimuleringV2BeregnetAfp(
    @field:Schema(description = "Totalbeløp (beløp i norske kroner)")
    val totalbelopAfp: Int?,

    @field:Schema(description = "Virkningsdato (fra og med) på formatet yyyy-MM-dd")
    val virkFom: LocalDate?,

    @field:Schema(description = "Tidligere arbeidsinntekt (beløp i norske kroner)")
    val tidligereArbeidsinntekt: Int?,

    @field:Schema(description = "Grunnbeløp (beløp i norske kroner)")
    val grunnbelop: Int?,

    @field:Schema(description = "Sluttpoengtall")
    val sluttpoengtall: Double?,

    @field:Schema(description = "Trygdetid i antall år")
    val trygdetid: Int?,

    @field:Schema(description = "Totalt antall poengår")
    val poengar: Int?,

    @field:Schema(description = "Antall poengår før 1992 (til og med 1991)")
    val poeangarF92: Int?,

    @field:Schema(description = "Antall poengår etter 1991 (fra og med 1992)")
    val poeangarE91: Int?,

    @field:Schema(description = "Grunnpensjon (beløp i norske kroner)")
    val grunnpensjon: Int?,

    @field:Schema(description = "Tilleggspensjon (beløp i norske kroner)")
    val tilleggspensjon: Int?,

    @field:Schema(description = "AFP-tillegg (beløp i norske kroner)")
    val afpTillegg: Int?,

    @field:Schema(description = "Framtidige pensjonspoeng")
    val fpp: Double?,

    @field:Schema(description = "Særtillegg (beløp i norske kroner)")
    val saertillegg: Int?,

    @field:Schema(description = "AFP-grad")
    val afpGrad: Int?,

    @field:Schema(description = "Hvorvidt den avtalefestede pensjonen er avkortet")
    val erAvkortet: Boolean?,
) {
    companion object {
        fun transferable(source: BeregnetAfp) =
            SimuleringV2BeregnetAfp(
                totalbelopAfp = source.totalbelopAfp,
                virkFom = source.virkFom,
                tidligereArbeidsinntekt = source.tidligereArbeidsinntekt,
                grunnbelop = source.grunnbelop,
                sluttpoengtall = source.sluttpoengtall,
                trygdetid = source.trygdetid,
                poengar = source.poengar,
                poeangarF92 = source.poeangarF92,
                poeangarE91 = source.poeangarE91,
                grunnpensjon = source.grunnpensjon,
                tilleggspensjon = source.tilleggspensjon,
                afpTillegg = source.afpTillegg,
                fpp = source.fpp,
                saertillegg = source.sertillegg,
                afpGrad = source.grad,
                erAvkortet = source.erAvkortet
            )
    }
}