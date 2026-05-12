package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.map

import no.nav.pensjon.kalkulator.afp.*
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.*
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import no.nav.pensjon.kalkulator.simulering.client.simulator.acl.spec.SivilstandSpecDto
import no.nav.pensjon.kalkulator.simulering.client.simulator.acl.spec.UtlandSpecDto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.UtenlandsperiodeForSimuleringDto

object ServiceberegnetAfpMapper {

    private const val SIMULERINGSTYPE = "AFP_FPP"

    fun toDto(spec: ServiceberegnetAfpSpec) =
        ServiceberegnetAfpSpecDto(
            simuleringstype = SIMULERINGSTYPE,
            uttaksdato = spec.uttaksdato,
            personopplysninger = PersonopplysningerDto(
                ident = spec.fnr,
                fodselsdato = spec.fodselsdato,
                valgtAfpOrdning = spec.afpOrdning.name,
                flyktning = spec.flyktning,
                antAarIUtlandet = spec.antAarIUtlandet,
                utenlandsopphold = spec.utenlandsopphold?.map { UtlandSpecDto( it.fom, it.tom, it.land.name, it.arbeidet) },
                forventetArbeidsinntekt = spec.forventetArbeidsinntekt,
                inntektMndForAfp = spec.inntektMndForAfp,
                erUnderUtdanning = false,
                epsData = mapEpsData(spec),
                avdodList = emptyList()
            ),
            barneopplysninger = null,
            opptjeningFolketrygden = OpptjeningFolketrygdenDto(
                egenOpptjeningFolketrygden = spec.opptjeningFolketrygden.map(::mapOpptjeningAar),
                avdodesOpptjeningFolketrygden = emptyList(),
                morsOpptjeningFolketrygden = emptyList(),
                farsOpptjeningFolketrygden = emptyList()
            )
        )

    private fun mapEpsData(spec: ServiceberegnetAfpSpec): EpsDataDto? =
        if (spec.epsMottarPensjon != null || spec.epsInntektOver2G != null || spec.sivilstatus != null)
            EpsDataDto(
                valgtSivilstatus = spec.sivilstatus?.let { SivilstatusTypeDto.fromInternalValue(it).name },
                registrertSivilstatus = spec.registrertSivilstatus?.let { SivilstandSpecDto.fromInternalValue(it).name },
                epsMottarPensjon = spec.epsMottarPensjon,
                epsInntektOver2G = spec.epsInntektOver2G,
                tidligereGiftEllerBarnMedSamboer = spec.tidligereGiftEllerBarnMedSamboer,
                erEpsInntektOver1G = true

            )
        else null

    private fun mapOpptjeningAar(opptjening: OpptjeningAar) =
        OpptjeningAarDto(
            ar = opptjening.ar,
            pensjonsgivendeInntekt = opptjening.pensjonsgivendeInntekt,
            omsorgspoeng = opptjening.omsorgspoeng,
            maksUforegrad = opptjening.maksUforegrad,
            registrertePensjonspoeng = opptjening.registrertePensjonspoeng
        )

    fun fromDto(dto: ServiceberegnetAfpResultDto) =
        ServiceberegnetAfpResult(
            afpOrdning = dto.afpOrdning?.let { AfpOrdningType.valueOf(it) },
            beregnetAfp = dto.beregnetAfp?.let(::mapBeregnetAfp),
            problem = dto.problem?.let(::mapProblem)
        )

    private fun mapBeregnetAfp(dto: BeregnetAfpDto) =
        BeregnetAfp(
            totalbelopAfp = dto.totalbelopAfp,
            virkFom = dto.virkFom,
            tidligereArbeidsinntekt = dto.tidligereArbeidsinntekt,
            grunnbelop = dto.grunnbelop,
            sluttpoengtall = dto.sluttpoengtall,
            trygdetid = dto.trygdetid,
            poengar = dto.poengar,
            poeangarF92 = dto.poeangar_f92,
            poeangarE91 = dto.poeangar_e91,
            grunnpensjon = dto.grunnpensjon,
            tilleggspensjon = dto.tilleggspensjon,
            afpTillegg = dto.afpTillegg,
            fpp = dto.fpp,
            sertillegg = dto.sertillegg,
            grad = dto.grad,
            erAvkortet = dto.erAvkortet
        )

    private fun mapProblem(dto: ServiceberegnetAfpProblemDto) =
        ServiceberegnetAfpProblem(
            type = dto.type?.let { runCatching { ServiceberegnetAfpProblemType.valueOf(it) }.getOrNull() }
                ?: ServiceberegnetAfpProblemType.ANNEN_KLIENTFEIL,
            beskrivelse = dto.beskrivelse ?: "Ukjent feil"
        )
}
