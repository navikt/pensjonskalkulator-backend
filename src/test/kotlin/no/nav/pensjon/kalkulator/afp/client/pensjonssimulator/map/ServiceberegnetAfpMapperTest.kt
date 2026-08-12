package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.afp.BeregnetAfp
import no.nav.pensjon.kalkulator.afp.OpptjeningAar
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblem
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblemType
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpResult
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpSpec
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.BeregnetAfpDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.EpsDataDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.FppUtlandPeriodeDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.OpptjeningAarDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.OpptjeningFolketrygdenDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.PersonopplysningerDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegnetAfpProblemDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegnetAfpResultDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegnetAfpSpecDto
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import no.nav.pensjon.kalkulator.simulering.Opphold
import java.time.LocalDate

class ServiceberegnetAfpMapperTest : ShouldSpec({

    context("toDto") {
        should("map all values and use simuleringstype 'AFP_FPP'") {
            ServiceberegnetAfpMapper.toDto(
                ServiceberegnetAfpSpec(
                    uttaksdato = LocalDate.of(2020, 1, 1),
                    fnr = pid.value,
                    fodselsdato = LocalDate.of(1970, 2, 15),
                    afpOrdning = AfpOrdningType.FINANS,
                    flyktning = true,
                    antAarIUtlandet = 1,
                    utenlandsopphold = listOf(
                        Opphold(
                            fom = LocalDate.of(1990, 1, 1),
                            tom = LocalDate.of(1991, 12, 31),
                            land = Land.ARG,
                            arbeidet = false
                        )
                    ),
                    forventetArbeidsinntekt = 2,
                    inntektMndForAfp = 3,
                    opptjeningFolketrygden = listOf(
                        OpptjeningAar(
                            ar = 2000,
                            pensjonsgivendeInntekt = 4,
                            omsorgspoeng = 5.1,
                            maksUforegrad = 6,
                            registrertePensjonspoeng = 7.2
                        )
                    ),
                    epsMottarPensjon = true,
                    epsInntektOver2G = false,
                    tidligereGiftEllerBarnMedSamboer = true,
                    sivilstatus = Sivilstatus.SAMBOER,
                    registrertSivilstatus = Sivilstand.UGIFT
                )
            ) shouldBe ServiceberegnetAfpSpecDto(
                simuleringstype = "AFP_FPP", // hard-coded
                uttaksdato = LocalDate.of(2020, 1, 1),
                personopplysninger = PersonopplysningerDto(
                    ident = pid.value,
                    fodselsdato = LocalDate.of(1970, 2, 15),
                    valgtAfpOrdning = AfpOrdningType.FINANS.name,
                    flyktning = true,
                    antAarIUtlandet = 1,
                    utenlandsopphold = listOf(
                        FppUtlandPeriodeDto(
                            fom = LocalDate.of(1990, 1, 1),
                            tom = LocalDate.of(1991, 12, 31),
                            land = Land.ARG.name,
                            arbeidetUtenlands = false
                        )
                    ),
                    forventetArbeidsinntekt = 2,
                    inntektMndForAfp = 3,
                    erUnderUtdanning = false, // hard-coded
                    epsData = EpsDataDto(
                        valgtSivilstatus = SivilstatusTypeDto.SAMB.name,
                        registrertSivilstatus = FppSivilstandDto.UGIF.name,
                        epsMottarPensjon = true,
                        epsInntektOver2G = false,
                        tidligereGiftEllerBarnMedSamboer = true,
                        erEpsInntektOver1G = true // hard-coded
                    ),
                    avdodList = emptyList() // hard-coded
                ),
                barneopplysninger = null, // hard-coded
                opptjeningFolketrygden = OpptjeningFolketrygdenDto(
                    egenOpptjeningFolketrygden = listOf(
                        OpptjeningAarDto(
                            ar = 2000,
                            pensjonsgivendeInntekt = 4,
                            omsorgspoeng = 5.1,
                            maksUforegrad = 6,
                            registrertePensjonspoeng = 7.2
                        )
                    ),
                    avdodesOpptjeningFolketrygden = emptyList(), // hard-coded
                    morsOpptjeningFolketrygden = emptyList(), // hard-coded
                    farsOpptjeningFolketrygden = emptyList() // hard-coded
                )
            )
        }
    }

    context("fromDto") {
        context("success") {
            should("map all non-problem values") {
                ServiceberegnetAfpMapper.fromDto(
                    dto = ServiceberegnetAfpResultDto(
                        afpOrdning = "AFPKOM",
                        beregnetAfp = BeregnetAfpDto(
                            totalbelopAfp = 1,
                            virkFom = LocalDate.of(2021, 2, 3),
                            tidligereArbeidsinntekt = 2,
                            grunnbelop = 3,
                            sluttpoengtall = 4.1,
                            trygdetid = 5,
                            poengar = 6,
                            poeangar_f92 = 7,
                            poeangar_e91 = 8,
                            grunnpensjon = 9,
                            tilleggspensjon = 10,
                            afpTillegg = 11,
                            fpp = 12.2,
                            sertillegg = 13,
                            grad = 14,
                            erAvkortet = true
                        ),
                        problem = null
                    )
                ) shouldBe ServiceberegnetAfpResult(
                    afpOrdning = AfpOrdningType.AFPKOM,
                    beregnetAfp = BeregnetAfp(
                        totalbelopAfp = 1,
                        virkFom = LocalDate.of(2021, 2, 3),
                        tidligereArbeidsinntekt = 2,
                        grunnbelop = 3,
                        sluttpoengtall = 4.1,
                        trygdetid = 5,
                        poengar = 6,
                        poeangarF92 = 7,
                        poeangarE91 = 8,
                        grunnpensjon = 9,
                        tilleggspensjon = 10,
                        afpTillegg = 11,
                        fpp = 12.2,
                        sertillegg = 13,
                        grad = 14,
                        erAvkortet = true
                    ),
                    problem = null
                )
            }
        }

        context("problem") {
            should("map problem-describing values") {
                ServiceberegnetAfpMapper.fromDto(
                    dto = ServiceberegnetAfpResultDto(
                        afpOrdning = null,
                        beregnetAfp = null,
                        problem = ServiceberegnetAfpProblemDto(
                            type = "UTILSTREKKELIG_OPPTJENING",
                            beskrivelse = "feil"
                        )
                    )
                ) shouldBe ServiceberegnetAfpResult(
                    afpOrdning = null,
                    beregnetAfp = null,
                    problem = ServiceberegnetAfpProblem(
                        type = ServiceberegnetAfpProblemType.UTILSTREKKELIG_OPPTJENING,
                        beskrivelse = "feil"
                    )
                )
            }
        }
    }
})