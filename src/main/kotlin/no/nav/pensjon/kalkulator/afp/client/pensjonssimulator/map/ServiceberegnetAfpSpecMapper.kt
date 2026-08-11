package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.map

import no.nav.pensjon.kalkulator.afp.*
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.*
import no.nav.pensjon.kalkulator.simulering.Opphold

object ServiceberegnetAfpSpecMapper {

    private const val SIMULERINGSTYPE = "AFP_FPP"

    fun toDto(spec: ServiceberegnetAfpSpec) =
        ServiceberegnetAfpSpecDto(
            simuleringstype = SIMULERINGSTYPE,
            uttaksdato = spec.uttaksdato,
            personopplysninger = PersonopplysningerDto(
                ident = spec.fnr,
                fodselsdato = spec.fodselsdato,
                valgtAfpOrdning = spec.afpOrdning, //TODO AfpOrdningDto::fromInternalValue
                flyktning = spec.flyktning,
                antAarIUtlandet = spec.antAarIUtlandet,
                utenlandsopphold = spec.utenlandsopphold?.map(::utlandPeriode),
                forventetArbeidsinntekt = spec.forventetArbeidsinntekt,
                inntektMndForAfp = spec.inntektMndForAfp,
                erUnderUtdanning = false,
                epsData = epsData(spec),
                avdodList = emptyList()
            ),
            barneopplysninger = null,
            opptjeningFolketrygden = OpptjeningFolketrygdenDto(
                egenOpptjeningFolketrygden = spec.opptjeningFolketrygden.map(::opptjeningAar),
                avdodesOpptjeningFolketrygden = emptyList(),
                morsOpptjeningFolketrygden = emptyList(),
                farsOpptjeningFolketrygden = emptyList()
            )
        )

    private fun epsData(spec: ServiceberegnetAfpSpec): EpsDataDto? =
        spec.minimumEpsData?.let {
            EpsDataDto(
                valgtSivilstatus = it.sivilstatus?.let(SivilstatusTypeDto::fromInternalValue),
                registrertSivilstatus = spec.registrertSivilstatus?.let(FppSivilstandDto::fromInternalValue),
                epsMottarPensjon = it.mottarPensjon,
                epsInntektOver2G = it.harInntektOver2G,
                tidligereGiftEllerBarnMedSamboer = spec.tidligereGiftEllerBarnMedSamboer,
                erEpsInntektOver1G = true
            )
        }

    private fun opptjeningAar(opptjening: OpptjeningAar) =
        OpptjeningAarDto(
            ar = opptjening.ar,
            pensjonsgivendeInntekt = opptjening.pensjonsgivendeInntekt,
            omsorgspoeng = opptjening.omsorgspoeng,
            maksUforegrad = opptjening.maksUforegrad,
            registrertePensjonspoeng = opptjening.registrertePensjonspoeng
        )

    private fun utlandPeriode(opphold: Opphold) =
        FppUtlandPeriodeDto(
            fom = opphold.fom,
            tom = opphold.tom,
            land = opphold.land, //TODO LandDto::fromInternalValue
            arbeidetUtenlands = opphold.arbeidet
        )
}