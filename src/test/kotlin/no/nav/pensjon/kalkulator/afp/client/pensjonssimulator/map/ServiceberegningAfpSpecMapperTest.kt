package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.afp.OpptjeningAar
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpSpec
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.EpsDataDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.OpptjeningFolketrygdenDataDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.PersonopplysningerDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.RelasjonDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegningAfpSpecDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.UtlandSpecDto
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import no.nav.pensjon.kalkulator.simulering.Opphold
import java.time.LocalDate

class ServiceberegningAfpSpecMapperTest : ShouldSpec({

    context("toDto") {
        should("map all values") {
            ServiceberegningAfpSpecMapper.toDto(
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
            ) shouldBe ServiceberegningAfpSpecDto(
                uttakFom = LocalDate.of(2020, 1, 1),
                personopplysninger = PersonopplysningerDto(
                    pid = pid.value,
                    foedselsdato = LocalDate.of(1970, 2, 15),
                    angittAfpOrdning = AfpOrdningType.FINANS.name,
                    flyktning = true,
                    antallAarUtenlands = 1,
                    utenlandsoppholdListe = listOf(
                        UtlandSpecDto(
                            fom = LocalDate.of(1990, 1, 1),
                            tom = LocalDate.of(1991, 12, 31),
                            land = Land.ARG.name,
                            arbeidetUtenlands = false
                        )
                    ),
                    forventetArbeidsinntekt = 2,
                    inntektMaanedenFoerAfp = 3,
                    eps = EpsDataDto(
                        relasjon = RelasjonDto(),
                        angittSivilstatus = SivilstatusTypeDto.SAMB.name,
                        registrertSivilstand = FppSivilstandDto.UGIF.name,
                        mottarPensjon = true,
                        harInntektOver1G = true, // hard-coded
                        harInntektOver2G = false,
                        tidligereGiftEllerBarnMedSamboer = true
                    )
                ),
                opptjeningListe = listOf(
                    OpptjeningFolketrygdenDataDto(
                        aar = 2000,
                        pensjonsgivendeInntekt = 4,
                        omsorgspoeng = 5.1,
                        registrertePensjonspoeng = 7.2,
                        maxUfoeregrad = 6
                    )
                )
            )
        }
    }
})