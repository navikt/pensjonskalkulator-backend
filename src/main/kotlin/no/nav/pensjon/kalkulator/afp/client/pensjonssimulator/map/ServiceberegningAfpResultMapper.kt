package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.map

import no.nav.pensjon.kalkulator.afp.BeregnetAfp
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblem
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblemType
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpResult
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegningAfpResultDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegningFolketrygdberegnetAfpDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegningOpptjeningDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegningProblemDto
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening

object ServiceberegningAfpResultMapper {

    fun fromDto(dto: ServiceberegningAfpResultDto) =
        ServiceberegnetAfpResult(
            afpOrdning = null, //TODO sjekk om behøves
            beregnetAfp = dto.beregnetAfp?.let(::mapBeregnetAfp),
            opptjeningListe = dto.opptjeningListe.map(::opptjening),
            problem = dto.problem?.let(::mapProblem)
        )

    private fun mapBeregnetAfp(dto: ServiceberegningFolketrygdberegnetAfpDto) =
        BeregnetAfp(
            totalbelopAfp = dto.afpTotalbeloep,
            virkFom = dto.virkningFom,
            tidligereArbeidsinntekt = dto.tidligereArbeidsinntekt,
            grunnbelop = dto.grunnbeloep,
            sluttpoengtall = dto.sluttpoengtall,
            trygdetid = dto.trygdetid,
            poengar = dto.poengaar,
            poeangarF92 = dto.poengaarFoer1992,
            poeangarE91 = dto.poengaarEtter1991,
            grunnpensjon = dto.grunnpensjon,
            tilleggspensjon = dto.tilleggspensjon,
            afpTillegg = dto.afpTillegg,
            fpp = dto.fpp,
            sertillegg = dto.saertillegg,
            grad = null, //TODO sjekk om behøves
            erAvkortet = null //TODO sjekk om behøves
        )

    private fun opptjening(dto: ServiceberegningOpptjeningDto) =
        AarligOpptjening(
            aar = dto.aarstall,
            pensjonsgivendeInntekt = dto.pensjonsgivendeInntekt,
            pensjonspoeng = dto.pensjonspoeng,
            omsorgspoeng = null,
            maksimalUfoeregrad = null,
            pensjonspoengType = "",
            beholdning = 0,
            merknadListe = emptyList()
        )

    private fun mapProblem(dto: ServiceberegningProblemDto) =
        ServiceberegnetAfpProblem(
            type = dto.type.let { runCatching { ServiceberegnetAfpProblemType.valueOf(it) }.getOrNull() }
                ?: ServiceberegnetAfpProblemType.ANNEN_KLIENTFEIL,
            beskrivelse = dto.beskrivelse
        )
}