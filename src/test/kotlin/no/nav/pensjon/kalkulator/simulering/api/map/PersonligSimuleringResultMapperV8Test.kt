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
                        pensjonBeholdningFoerUttak = 5
                    )
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(SimulertAfpPrivat(alder = 67, beloep = 12000)),
                afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 12000)),
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
            afpPrivat = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000)),
            afpOffentlig = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000)),
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
                        pensjonBeholdningFoerUttak = 5
                    ),
                    SimulertAlderspensjon(
                        alder = 68,
                        beloep = 123456,
                        inntektspensjonBeloep = 1,
                        garantipensjonBeloep = 2,
                        delingstall = 3.4,
                        pensjonBeholdningFoerUttak = 5
                    ),
                    SimulertAlderspensjon(
                        alder = 0,
                        beloep = 1,
                        inntektspensjonBeloep = 2,
                        garantipensjonBeloep = 3,
                        delingstall = 4.5,
                        pensjonBeholdningFoerUttak = 6
                    )
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(SimulertAfpPrivat(alder = 67, beloep = 12000)),
                afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 12000)),
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
            afpPrivat = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000)),
            afpOffentlig = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000)),
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
                        pensjonBeholdningFoerUttak = 5
                    ),
                    SimulertAlderspensjon(
                        alder = 69,
                        beloep = 123456,
                        inntektspensjonBeloep = 1,
                        garantipensjonBeloep = 2,
                        delingstall = 3.4,
                        pensjonBeholdningFoerUttak = 5
                    ),
                    SimulertAlderspensjon(
                        alder = 0,
                        beloep = 1,
                        inntektspensjonBeloep = 2,
                        garantipensjonBeloep = 3,
                        delingstall = 4.5,
                        pensjonBeholdningFoerUttak = 6
                    )
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(SimulertAfpPrivat(alder = 67, beloep = 12000)),
                afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 12000)),
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
            afpPrivat = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000)),
            afpOffentlig = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000)),
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
                        pensjonBeholdningFoerUttak = 5
                    ),
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(
                    SimulertAfpPrivat(alder = 67, beloep = 12000),
                    SimulertAfpPrivat(alder = 68, beloep = 13000),
                    SimulertAfpPrivat(alder = 0, beloep = 14000),
                    ),
                afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 12000)),
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
                PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000),
                PersonligSimuleringAarligPensjonResultV8(alder = 68, beloep = 13000)
            ),
            afpOffentlig = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000)),
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
                        pensjonBeholdningFoerUttak = 5
                    ),
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(
                    SimulertAfpPrivat(alder = 68, beloep = 12000),
                    SimulertAfpPrivat(alder = 69, beloep = 13000),
                    SimulertAfpPrivat(alder = 0, beloep = 14000),
                ),
                afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 12000)),
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
                PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 14000),
                PersonligSimuleringAarligPensjonResultV8(alder = 68, beloep = 12000),
                PersonligSimuleringAarligPensjonResultV8(alder = 69, beloep = 13000)
            ),
            afpOffentlig = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000)),
            vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV8(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = null,
            opptjeningGrunnlagListe = null
        )
    }
}