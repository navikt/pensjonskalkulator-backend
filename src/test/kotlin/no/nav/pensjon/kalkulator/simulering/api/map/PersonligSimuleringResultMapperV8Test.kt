package no.nav.pensjon.kalkulator.simulering.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PersonligSimuleringResultMapperV8Test{
    @Test
    fun `resultatV8 maps domain to V8 DTO`() {
        PersonligSimuleringResultMapperV8.resultV8(
            SimuleringResult(
                alderspensjon = listOf(
                    SimulertAlderspensjon(
                        alder = 67,
                        beloep = 123456,
                        inntektspensjonBeloep = 1,
                        garantipensjonBeloep = 2,
                        delingstall = 3.4,
                        pensjonBeholdningFoerUttak = 5,
                        andelsbroekKap19 = 0.6,
                        andelsbroekKap20 = 0.4,
                        sluttpoengtall = 5.11,
                        trygdetidKap19 = 40,
                        trygdetidKap20 = 40,
                        poengaarFoer92 = 13,
                        poengaarEtter91 = 27,
                        forholdstall = 0.971,
                        grunnpensjon = 55810,
                        tilleggspensjon = 134641,
                        pensjonstillegg = -70243,
                        skjermingstillegg = 0
                    )
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(SimulertAfpPrivat(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
                afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
                vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                harForLiteTrygdetid = true,
                trygdetid = 10,
                opptjeningGrunnlagListe = listOf(
                    SimulertOpptjeningGrunnlag(aar = 2001, pensjonsgivendeInntektBeloep = 501000),
                    SimulertOpptjeningGrunnlag(aar = 2002, pensjonsgivendeInntektBeloep = 502000)
                )
            ),
            LocalDate.now().minusYears(67).minusMonths(1)
        ) shouldBe PersonligSimuleringResultV8(
            alderspensjon = listOf(
                PersonligSimuleringAlderspensjonResultV8(
                    alder = 67,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                )
            ),
            alderspensjonMaanedligVedEndring = PersonligSimuleringMaanedligPensjonResultV8(
                gradertUttakMaanedligBeloep = 6,
                heltUttakMaanedligBeloep = 7
            ),
            afpPrivat = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
            afpOffentlig = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
            vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV8(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = null,
            opptjeningGrunnlagListe = null
        )
    }

    @Test
    fun `resultatV8 ignores alderspensjon with age 0 when mapping domain to V8 DTO`() {
        PersonligSimuleringResultMapperV8.resultV8(
            SimuleringResult(
                alderspensjon = listOf(
                    SimulertAlderspensjon(
                        alder = 67,
                        beloep = 123456,
                        inntektspensjonBeloep = 1,
                        garantipensjonBeloep = 2,
                        delingstall = 3.4,
                        pensjonBeholdningFoerUttak = 5,
                        andelsbroekKap19 = 0.6,
                        andelsbroekKap20 = 0.4,
                        sluttpoengtall = 5.11,
                        trygdetidKap19 = 40,
                        trygdetidKap20 = 40,
                        poengaarFoer92 = 13,
                        poengaarEtter91 = 27,
                        forholdstall = 0.971,
                        grunnpensjon = 55810,
                        tilleggspensjon = 134641,
                        pensjonstillegg = -70243,
                        skjermingstillegg = 0
                    ),
                    SimulertAlderspensjon(
                        alder = 68,
                        beloep = 123456,
                        inntektspensjonBeloep = 1,
                        garantipensjonBeloep = 2,
                        delingstall = 3.4,
                        pensjonBeholdningFoerUttak = 5,
                        andelsbroekKap19 = 0.6,
                        andelsbroekKap20 = 0.4,
                        sluttpoengtall = 5.11,
                        trygdetidKap19 = 40,
                        trygdetidKap20 = 40,
                        poengaarFoer92 = 13,
                        poengaarEtter91 = 27,
                        forholdstall = 0.971,
                        grunnpensjon = 55810,
                        tilleggspensjon = 134641,
                        pensjonstillegg = -70243,
                        skjermingstillegg = 0
                    ),
                    SimulertAlderspensjon(
                        alder = 0,
                        beloep = 1,
                        inntektspensjonBeloep = 2,
                        garantipensjonBeloep = 3,
                        delingstall = 4.5,
                        pensjonBeholdningFoerUttak = 6,
                        andelsbroekKap19 = 0.6,
                        andelsbroekKap20 = 0.4,
                        sluttpoengtall = 5.11,
                        trygdetidKap19 = 40,
                        trygdetidKap20 = 40,
                        poengaarFoer92 = 13,
                        poengaarEtter91 = 27,
                        forholdstall = 0.971,
                        grunnpensjon = 55810,
                        tilleggspensjon = 134641,
                        pensjonstillegg = -70243,
                        skjermingstillegg = 0
                    )
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(SimulertAfpPrivat(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
                afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
                vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                harForLiteTrygdetid = true,
                trygdetid = 10,
                opptjeningGrunnlagListe = listOf(
                    SimulertOpptjeningGrunnlag(aar = 2001, pensjonsgivendeInntektBeloep = 501000),
                    SimulertOpptjeningGrunnlag(aar = 2002, pensjonsgivendeInntektBeloep = 502000)
                )
            ),
            LocalDate.now().minusYears(67).minusMonths(1)
        ) shouldBe PersonligSimuleringResultV8(
            alderspensjon = listOf(
                PersonligSimuleringAlderspensjonResultV8(
                    alder = 67,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                ),
                PersonligSimuleringAlderspensjonResultV8(
                    alder = 68,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                )
            ),
            alderspensjonMaanedligVedEndring = PersonligSimuleringMaanedligPensjonResultV8(
                gradertUttakMaanedligBeloep = 6,
                heltUttakMaanedligBeloep = 7
            ),
            afpPrivat = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
            afpOffentlig = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
            vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV8(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = null,
            opptjeningGrunnlagListe = null
        )
    }

    @Test
    fun `resultatV8 assigns 0 age to current age and adds it to the list, when mapping domain to V8 DTO`() {
        PersonligSimuleringResultMapperV8.resultV8(
            SimuleringResult(
                alderspensjon = listOf(
                    SimulertAlderspensjon(
                        alder = 68,
                        beloep = 123456,
                        inntektspensjonBeloep = 1,
                        garantipensjonBeloep = 2,
                        delingstall = 3.4,
                        pensjonBeholdningFoerUttak = 5,
                        andelsbroekKap19 = 0.6,
                        andelsbroekKap20 = 0.4,
                        sluttpoengtall = 5.11,
                        trygdetidKap19 = 40,
                        trygdetidKap20 = 40,
                        poengaarFoer92 = 13,
                        poengaarEtter91 = 27,
                        forholdstall = 0.971,
                        grunnpensjon = 55810,
                        tilleggspensjon = 134641,
                        pensjonstillegg = -70243,
                        skjermingstillegg = 0
                    ),
                    SimulertAlderspensjon(
                        alder = 69,
                        beloep = 123456,
                        inntektspensjonBeloep = 1,
                        garantipensjonBeloep = 2,
                        delingstall = 3.4,
                        pensjonBeholdningFoerUttak = 5,
                        andelsbroekKap19 = 0.6,
                        andelsbroekKap20 = 0.4,
                        sluttpoengtall = 5.11,
                        trygdetidKap19 = 40,
                        trygdetidKap20 = 40,
                        poengaarFoer92 = 13,
                        poengaarEtter91 = 27,
                        forholdstall = 0.971,
                        grunnpensjon = 55810,
                        tilleggspensjon = 134641,
                        pensjonstillegg = -70243,
                        skjermingstillegg = 0
                    ),
                    SimulertAlderspensjon(
                        alder = 0,
                        beloep = 1,
                        inntektspensjonBeloep = 2,
                        garantipensjonBeloep = 3,
                        delingstall = 4.5,
                        pensjonBeholdningFoerUttak = 6,
                        andelsbroekKap19 = 0.6,
                        andelsbroekKap20 = 0.4,
                        sluttpoengtall = 5.11,
                        trygdetidKap19 = 40,
                        trygdetidKap20 = 40,
                        poengaarFoer92 = 13,
                        poengaarEtter91 = 27,
                        forholdstall = 0.971,
                        grunnpensjon = 55810,
                        tilleggspensjon = 134641,
                        pensjonstillegg = -70243,
                        skjermingstillegg = 0
                    )
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(SimulertAfpPrivat(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
                afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
                vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                harForLiteTrygdetid = true,
                trygdetid = 10,
                opptjeningGrunnlagListe = listOf(
                    SimulertOpptjeningGrunnlag(aar = 2001, pensjonsgivendeInntektBeloep = 501000),
                    SimulertOpptjeningGrunnlag(aar = 2002, pensjonsgivendeInntektBeloep = 502000)
                )
            ),
            LocalDate.now().minusYears(67).minusMonths(1)
        ) shouldBe PersonligSimuleringResultV8(
            alderspensjon = listOf(
                PersonligSimuleringAlderspensjonResultV8(
                    alder = 67,
                    beloep = 1,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                ),
                PersonligSimuleringAlderspensjonResultV8(
                    alder = 68,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                ),
                PersonligSimuleringAlderspensjonResultV8(
                    alder = 69,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                )
            ),
            alderspensjonMaanedligVedEndring = PersonligSimuleringMaanedligPensjonResultV8(
                gradertUttakMaanedligBeloep = 6,
                heltUttakMaanedligBeloep = 7
            ),
            afpPrivat = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
            afpOffentlig = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
            vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV8(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = null,
            opptjeningGrunnlagListe = null
        )
    }

    @Test
    fun `resultatV8 filters away Afp Privat with age 0 when mapping domain to V8 DTO`() {
        PersonligSimuleringResultMapperV8.resultV8(
            SimuleringResult(
                alderspensjon = listOf(
                    SimulertAlderspensjon(
                        alder = 67,
                        beloep = 123456,
                        inntektspensjonBeloep = 1,
                        garantipensjonBeloep = 2,
                        delingstall = 3.4,
                        pensjonBeholdningFoerUttak = 5,
                        andelsbroekKap19 = 0.6,
                        andelsbroekKap20 = 0.4,
                        sluttpoengtall = 5.11,
                        trygdetidKap19 = 40,
                        trygdetidKap20 = 40,
                        poengaarFoer92 = 13,
                        poengaarEtter91 = 27,
                        forholdstall = 0.971,
                        grunnpensjon = 55810,
                        tilleggspensjon = 134641,
                        pensjonstillegg = -70243,
                        skjermingstillegg = 0
                    ),
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(
                    SimulertAfpPrivat(alder = 67, beloep = 12000, maanedligBeloep = 1000),
                    SimulertAfpPrivat(alder = 68, beloep = 13000, maanedligBeloep = 1000),
                    SimulertAfpPrivat(alder = 0, beloep = 14000, maanedligBeloep = 1000),
                    ),
                afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
                vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                harForLiteTrygdetid = true,
                trygdetid = 10,
                opptjeningGrunnlagListe = listOf(
                    SimulertOpptjeningGrunnlag(aar = 2001, pensjonsgivendeInntektBeloep = 501000),
                    SimulertOpptjeningGrunnlag(aar = 2002, pensjonsgivendeInntektBeloep = 502000)
                )
            ),
            LocalDate.now().minusYears(67).minusMonths(1)
        ) shouldBe PersonligSimuleringResultV8(
            alderspensjon = listOf(
                PersonligSimuleringAlderspensjonResultV8(
                    alder = 67,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                ),
            ),
            alderspensjonMaanedligVedEndring = PersonligSimuleringMaanedligPensjonResultV8(
                gradertUttakMaanedligBeloep = 6,
                heltUttakMaanedligBeloep = 7
            ),
            afpPrivat = listOf(
                PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000, maanedligBeloep = 1000),
                PersonligSimuleringAarligPensjonResultV8(alder = 68, beloep = 13000, maanedligBeloep = 1000)
            ),
            afpOffentlig = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
            vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV8(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = null,
            opptjeningGrunnlagListe = null
        )
    }

    @Test
    fun `resultatV8 assigns Afp Privat at 0 age to current age and adds it to the list, when mapping domain to V8 DTO`() {
        PersonligSimuleringResultMapperV8.resultV8(
            SimuleringResult(
                alderspensjon = listOf(
                    SimulertAlderspensjon(
                        alder = 67,
                        beloep = 123456,
                        inntektspensjonBeloep = 1,
                        garantipensjonBeloep = 2,
                        delingstall = 3.4,
                        pensjonBeholdningFoerUttak = 5,
                        andelsbroekKap19 = 0.6,
                        andelsbroekKap20 = 0.4,
                        sluttpoengtall = 5.11,
                        trygdetidKap19 = 40,
                        trygdetidKap20 = 40,
                        poengaarFoer92 = 13,
                        poengaarEtter91 = 27,
                        forholdstall = 0.971,
                        grunnpensjon = 55810,
                        tilleggspensjon = 134641,
                        pensjonstillegg = -70243,
                        skjermingstillegg = 0
                    ),
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(
                    SimulertAfpPrivat(alder = 68, beloep = 12000, maanedligBeloep = 1000),
                    SimulertAfpPrivat(alder = 69, beloep = 13000, maanedligBeloep = 1000),
                    SimulertAfpPrivat(alder = 0, beloep = 14000, maanedligBeloep = 1000),
                ),
                afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
                vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                harForLiteTrygdetid = true,
                trygdetid = 10,
                opptjeningGrunnlagListe = listOf(
                    SimulertOpptjeningGrunnlag(aar = 2001, pensjonsgivendeInntektBeloep = 501000),
                    SimulertOpptjeningGrunnlag(aar = 2002, pensjonsgivendeInntektBeloep = 502000)
                )
            ),
            LocalDate.now().minusYears(67).minusMonths(1)
        ) shouldBe PersonligSimuleringResultV8(
            alderspensjon = listOf(
                PersonligSimuleringAlderspensjonResultV8(
                    alder = 67,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null,
                    andelsbroekKap19 = null,
                    andelsbroekKap20 = null,
                    sluttpoengtall = null,
                    trygdetidKap19 = null,
                    trygdetidKap20 = null,
                    poengaarFoer92 = null,
                    poengaarEtter91 = null,
                    forholdstall = null,
                    grunnpensjon = null,
                    tilleggspensjon = null,
                    pensjonstillegg = null,
                    skjermingstillegg = null
                ),
            ),
            alderspensjonMaanedligVedEndring = PersonligSimuleringMaanedligPensjonResultV8(
                gradertUttakMaanedligBeloep = 6,
                heltUttakMaanedligBeloep = 7
            ),
            afpPrivat = listOf(
                PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 14000, maanedligBeloep = 1000),
                PersonligSimuleringAarligPensjonResultV8(alder = 68, beloep = 12000, maanedligBeloep = 1000),
                PersonligSimuleringAarligPensjonResultV8(alder = 69, beloep = 13000, maanedligBeloep = 1000)
            ),
            afpOffentlig = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000, maanedligBeloep = 1000)),
            vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV8(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = null,
            opptjeningGrunnlagListe = null
        )
    }
}