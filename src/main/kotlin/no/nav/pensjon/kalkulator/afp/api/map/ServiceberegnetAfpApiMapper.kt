package no.nav.pensjon.kalkulator.afp.api.map

import no.nav.pensjon.kalkulator.afp.OpptjeningAar
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpResult
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpSpec
import no.nav.pensjon.kalkulator.afp.api.dto.*
import no.nav.pensjon.kalkulator.opptjening.Pensjonspoeng
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import java.time.LocalDate

object ServiceberegnetAfpApiMapper {

    fun fromDto(dto: InternServiceberegnetAfpSpec, pid: Pid, pensjonspoeng: List<Pensjonspoeng>, tidligereGiftEllerBarnMedSamboer: Boolean?, naavaerendeSivilstatus: Sivilstand) =
        ServiceberegnetAfpSpec(
            uttaksdato = dto.uttaksdato,
            fnr = pid.value,
            fodselsdato = dto.fodselsdato,
            afpOrdning = AfpOrdningType.valueOf(dto.afpOrdning),
            flyktning = dto.flyktning,
            antAarIUtlandet = dto.antAarIUtlandet,
            utenlandsopphold = dto.utenlandsopphold,
            forventetArbeidsinntekt = dto.forventetArbeidsinntekt,
            inntektMndForAfp = dto.inntektMndForAfp,
            opptjeningFolketrygden = pensjonspoeng.map(::mapOpptjeningAar) + mapInntektOpptjening(dto),
            epsMottarPensjon = dto.epsMottarPensjon,
            epsInntektOver2G = dto.epsInntektOver2G,
            tidligereGiftEllerBarnMedSamboer = tidligereGiftEllerBarnMedSamboer,
            sivilstatus = dto.sivilstatus,
            registrertSivilstatus = naavaerendeSivilstatus
        )

    private fun mapOpptjeningAar(dto: Pensjonspoeng) =
        OpptjeningAar(
            ar = dto.ar,
            pensjonsgivendeInntekt = dto.pensjonsgivendeInntekt,
            registrertePensjonspoeng = dto.pensjonspoeng,
            omsorgspoeng = dto.omsorgspoeng?.toDouble(),
            maksUforegrad = dto.maksUforegrad,
        )

    private fun mapInntektOpptjening(dto: InternServiceberegnetAfpSpec): List<OpptjeningAar> =
        listOfNotNull(dto.inntektForrigeKalenderaar?.let {
            OpptjeningAar(LocalDate.now().year - 1, it, registrertePensjonspoeng = null, omsorgspoeng = null, maksUforegrad = null)
        }) +
        (dto.inntektFremTilUttak?.let { inntekt ->
            (LocalDate.now().year until dto.uttaksdato.year).map { year ->
                OpptjeningAar(year, inntekt, registrertePensjonspoeng = null, omsorgspoeng = null, maksUforegrad = null)
            }
        } ?: emptyList())

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
