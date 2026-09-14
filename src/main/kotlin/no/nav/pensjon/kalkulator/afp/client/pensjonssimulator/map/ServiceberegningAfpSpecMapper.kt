package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.map

import no.nav.pensjon.kalkulator.afp.OpptjeningAar
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpSpec
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.*

object ServiceberegningAfpSpecMapper {

    fun toDto(spec: ServiceberegnetAfpSpec) =
        ServiceberegningAfpSpecDto(
            uttakFom = spec.uttaksdato,
            personopplysninger = PersonopplysningerDto(
                pid = spec.fnr,
                foedselsdato = spec.fodselsdato,
                angittAfpOrdning = spec.afpOrdning.name,
                flyktning = spec.flyktning,
                antallAarUtenlands = spec.antAarIUtlandet,
                utenlandsoppholdListe = spec.utenlandsopphold?.map { UtlandSpecDto(it.fom, it.tom, it.land.name, it.arbeidet) },
                forventetArbeidsinntekt = spec.forventetArbeidsinntekt,
                inntektMaanedenFoerAfp = spec.inntektMndForAfp,
                eps = mapEpsData(spec)
            ),
            opptjeningListe = spec.opptjeningFolketrygden.map(::mapOpptjeningAar)
        )

    private fun mapEpsData(spec: ServiceberegnetAfpSpec): EpsDataDto? =
        if (spec.epsMottarPensjon != null || spec.epsInntektOver2G != null || spec.sivilstatus != null)
            EpsDataDto(
                relasjon = RelasjonDto(), //TODO behøves trolig ikke
                angittSivilstatus = spec.sivilstatus?.let { SivilstatusTypeDto.fromInternalValue(it).name },
                registrertSivilstand = spec.registrertSivilstatus?.let { FppSivilstandDto.fromInternalValue(it).name },
                mottarPensjon = spec.epsMottarPensjon,
                harInntektOver1G = true,
                harInntektOver2G = spec.epsInntektOver2G,
                tidligereGiftEllerBarnMedSamboer = spec.tidligereGiftEllerBarnMedSamboer
            )
        else null

    private fun mapOpptjeningAar(opptjening: OpptjeningAar) =
        OpptjeningFolketrygdenDataDto(
            aar = opptjening.ar,
            pensjonsgivendeInntekt = opptjening.pensjonsgivendeInntekt,
            omsorgspoeng = opptjening.omsorgspoeng,
            registrertePensjonspoeng = opptjening.registrertePensjonspoeng,
            maxUfoeregrad = opptjening.maksUforegrad
        )
}