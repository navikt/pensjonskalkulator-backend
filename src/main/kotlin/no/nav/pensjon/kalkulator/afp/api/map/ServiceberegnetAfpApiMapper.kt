package no.nav.pensjon.kalkulator.afp.api.map

import no.nav.pensjon.kalkulator.afp.OpptjeningAar
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpResult
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpSpec
import no.nav.pensjon.kalkulator.afp.api.dto.*
import no.nav.pensjon.kalkulator.opptjening.Pensjonspoeng
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType

object ServiceberegnetAfpApiMapper {

    fun fromDto(dto: InternServiceberegnetAfpSpec, pid: Pid, pensjonspoeng: List<Pensjonspoeng>) =
        ServiceberegnetAfpSpec(
            uttaksdato = dto.uttaksdato,
            fnr = pid.value,
            fodselsdato = dto.fodselsdato,
            afpOrdning = AfpOrdningType.valueOf(dto.afpOrdning),
            flyktning = dto.flyktning,
            antAarIUtlandet = dto.antAarIUtlandet,
            forventetArbeidsinntekt = dto.forventetArbeidsinntekt,
            inntektMndForAfp = dto.inntektMndForAfp,
            opptjeningFolketrygden = pensjonspoeng.map(::mapOpptjeningAar)
        )

    private fun mapOpptjeningAar(dto: Pensjonspoeng) =
        OpptjeningAar(
            ar = dto.ar,
            pensjonsgivendeInntekt = dto.pensjonsgivendeInntekt,
            registrertePensjonspoeng = dto.pensjonspoeng,
            omsorgspoeng = dto.omsorgspoeng?.toDouble(),
            maksUforegrad = dto.maksUforegrad,
        )

    fun toDto(result: ServiceberegnetAfpResult) =
        InternServiceberegnetAfpResult(
            afpOrdning = result.afpOrdning?.name,
            beregnetAfp = result.beregnetAfp?.let {
                InternBeregnetAfp(
                    totalbelopAfp = it.totalbelopAfp,
                    virkFom = it.virkFom,
                    tidligereArbeidsinntekt = it.tidligereArbeidsinntekt,
                    grunnbelop = it.grunnbelop,
                    sluttpoengtall = it.sluttpoengtall,
                    trygdetid = it.trygdetid,
                    poengar = it.poengar,
                    poeangarF92 = it.poeangarF92,
                    poeangarE91 = it.poeangarE91,
                    grunnpensjon = it.grunnpensjon,
                    tilleggspensjon = it.tilleggspensjon,
                    afpTillegg = it.afpTillegg,
                    fpp = it.fpp,
                    sertillegg = it.sertillegg
                )
            },
            problem = result.problem?.let {
                InternServiceberegnetAfpProblem(
                    type = it.type.name,
                    beskrivelse = it.beskrivelse
                )
            }
        )
}
