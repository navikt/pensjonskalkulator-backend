package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.map

import no.nav.pensjon.kalkulator.afp.BeregnetAfp
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblem
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblemType
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpResult
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.BeregnetAfpDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegnetAfpProblemDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegnetAfpResultDto
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType

object ServiceberegnetAfpResultMapper {

    fun fromDto(dto: ServiceberegnetAfpResultDto) =
        ServiceberegnetAfpResult(
            afpOrdning = dto.afpOrdning?.let(AfpOrdningType::valueOf),
            beregnetAfp = dto.beregnetAfp?.let(::beregnetAfp),
            problem = dto.problem?.let(::problem)
        )

    private fun beregnetAfp(dto: BeregnetAfpDto) =
        BeregnetAfp(
            afpTotalbeloep = dto.totalbelopAfp,
            virkningFom = dto.virkFom,
            tidligereArbeidsinntekt = dto.tidligereArbeidsinntekt,
            grunnbeloep = dto.grunnbelop,
            sluttpoengtall = dto.sluttpoengtall,
            trygdetid = dto.trygdetid,
            poengaar = dto.poengar,
            poengaarFoer1992 = dto.poeangar_f92,
            poengaarEtter1991 = dto.poeangar_e91,
            grunnpensjon = dto.grunnpensjon,
            tilleggspensjon = dto.tilleggspensjon,
            afpTillegg = dto.afpTillegg,
            fpp = dto.fpp,
            saertillegg = dto.sertillegg,
            grad = dto.grad,
            erAvkortet = dto.erAvkortet
        )

    private fun problem(dto: ServiceberegnetAfpProblemDto) =
        ServiceberegnetAfpProblem(
            type = dto.type?.let { runCatching { ServiceberegnetAfpProblemType.valueOf(it) }.getOrNull() }
                ?: ServiceberegnetAfpProblemType.ANNEN_KLIENTFEIL,
            beskrivelse = dto.beskrivelse ?: "Ukjent feil"
        )
}