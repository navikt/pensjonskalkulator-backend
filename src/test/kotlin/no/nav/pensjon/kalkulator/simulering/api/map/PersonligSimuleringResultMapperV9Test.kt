package no.nav.pensjon.kalkulator.simulering.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import java.time.LocalDate

class PersonligSimuleringResultMapperV9Test : ShouldSpec({

    should("map domain to V9 DTO") {
        PersonligSimuleringResultMapperV9.resultV9(
            SimuleringResult(
                alderspensjon = listOf(alderspensjon(alder = 67, beloep = 123456)),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(privatAfp(alder = 67, beloep = 12000)),
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
        ) shouldBe PersonligSimuleringResultV9(
            alderspensjon = listOf(
                PersonligSimuleringAlderspensjonResultV9(
                    alder = 67,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                )
            ),
            alderspensjonMaanedligVedEndring = PersonligSimuleringMaanedligPensjonResultV9(
                gradertUttakMaanedligBeloep = 6,
                heltUttakMaanedligBeloep = 7
            ),
            afpPrivat = listOf(
                PersonligSimuleringAfpPrivatResultV9(
                    alder = 67,
                    beloep = 12000,
                    kompensasjonstillegg = 123,
                    kronetillegg = 69,
                    livsvarig = 321,
                    maanedligBeloep = 1000
                )
            ),
            afpOffentlig = listOf(
                PersonligSimuleringAarligPensjonResultV9(
                    alder = 67,
                    beloep = 12000,
                    maanedligBeloep = 1000
                )
            ),
            vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV9(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = null,
            opptjeningGrunnlagListe = null
        )
    }

    should("ignore alderspensjon with age 0 when mapping domain to V9 DTO") {
        PersonligSimuleringResultMapperV9.resultV9(
            SimuleringResult(
                alderspensjon = listOf(
                    alderspensjon(alder = 67, beloep = 123456),
                    alderspensjon(alder = 68, beloep = 123456),
                    alderspensjon(alder = 0, beloep = 1)
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(privatAfp(alder = 67, beloep = 12000)),
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
        ) shouldBe PersonligSimuleringResultV9(
            alderspensjon = listOf(
                PersonligSimuleringAlderspensjonResultV9(
                    alder = 67,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                ),
                PersonligSimuleringAlderspensjonResultV9(
                    alder = 68,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                )
            ),
            alderspensjonMaanedligVedEndring = PersonligSimuleringMaanedligPensjonResultV9(
                gradertUttakMaanedligBeloep = 6,
                heltUttakMaanedligBeloep = 7
            ),
            afpPrivat = listOf(
                PersonligSimuleringAfpPrivatResultV9(
                    alder = 67,
                    beloep = 12000,
                    kompensasjonstillegg = 123,
                    kronetillegg = 69,
                    livsvarig = 321,
                    maanedligBeloep = 1000
                )
            ),
            afpOffentlig = listOf(
                PersonligSimuleringAarligPensjonResultV9(
                    alder = 67,
                    beloep = 12000,
                    maanedligBeloep = 1000
                )
            ),
            vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV9(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = null,
            opptjeningGrunnlagListe = null
        )
    }

    should("assign 0 age to current age and adds it to the list, when mapping domain to V9 DTO") {
        PersonligSimuleringResultMapperV9.resultV9(
            SimuleringResult(
                alderspensjon = listOf(
                    alderspensjon(alder = 68, beloep = 123456),
                    alderspensjon(alder = 69, beloep = 123456),
                    alderspensjon(alder = 0, beloep = 1)
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(privatAfp(alder = 67, beloep = 12000)),
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
        ) shouldBe PersonligSimuleringResultV9(
            alderspensjon = listOf(
                PersonligSimuleringAlderspensjonResultV9(
                    alder = 67,
                    beloep = 1,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                ),
                PersonligSimuleringAlderspensjonResultV9(
                    alder = 68,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                ),
                PersonligSimuleringAlderspensjonResultV9(
                    alder = 69,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                )
            ),
            alderspensjonMaanedligVedEndring = PersonligSimuleringMaanedligPensjonResultV9(
                gradertUttakMaanedligBeloep = 6,
                heltUttakMaanedligBeloep = 7
            ),
            afpPrivat = listOf(
                PersonligSimuleringAfpPrivatResultV9(
                    alder = 67,
                    beloep = 12000,
                    kompensasjonstillegg = 123,
                    kronetillegg = 69,
                    livsvarig = 321,
                    maanedligBeloep = 1000
                )
            ),
            afpOffentlig = listOf(
                PersonligSimuleringAarligPensjonResultV9(
                    alder = 67,
                    beloep = 12000,
                    maanedligBeloep = 1000
                )
            ),
            vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV9(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = null,
            opptjeningGrunnlagListe = null
        )
    }

    should("filter away 'privat AFP' with age 0 when mapping domain to V9 DTO") {
        PersonligSimuleringResultMapperV9.resultV9(
            SimuleringResult(
                alderspensjon = listOf(alderspensjon(alder = 67, beloep = 123456)),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(
                    privatAfp(alder = 67, beloep = 12000),
                    privatAfp(alder = 68, beloep = 13000),
                    privatAfp(alder = 0, beloep = 14000)
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
        ) shouldBe PersonligSimuleringResultV9(
            alderspensjon = listOf(
                PersonligSimuleringAlderspensjonResultV9(
                    alder = 67,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                ),
            ),
            alderspensjonMaanedligVedEndring = PersonligSimuleringMaanedligPensjonResultV9(
                gradertUttakMaanedligBeloep = 6,
                heltUttakMaanedligBeloep = 7
            ),
            afpPrivat = listOf(
                PersonligSimuleringAfpPrivatResultV9(
                    alder = 67,
                    beloep = 12000,
                    kompensasjonstillegg = 123,
                    kronetillegg = 69,
                    livsvarig = 321,
                    maanedligBeloep = 1000
                ),
                PersonligSimuleringAfpPrivatResultV9(
                    alder = 68,
                    beloep = 13000,
                    kompensasjonstillegg = 123,
                    kronetillegg = 69,
                    livsvarig = 321,
                    maanedligBeloep = 1000
                )
            ),
            afpOffentlig = listOf(
                PersonligSimuleringAarligPensjonResultV9(
                    alder = 67,
                    beloep = 12000,
                    maanedligBeloep = 1000
                )
            ),
            vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV9(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = null,
            opptjeningGrunnlagListe = null
        )
    }

    should("assign 'privat AFP' at 0 age to current age and add it to the list, when mapping domain to V9 DTO") {
        PersonligSimuleringResultMapperV9.resultV9(
            SimuleringResult(
                alderspensjon = listOf(alderspensjon(alder = 67, beloep = 123456)),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(
                    privatAfp(alder = 68, beloep = 12000),
                    privatAfp(alder = 69, beloep = 13000),
                    privatAfp(alder = 0, beloep = 14000)
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
        ) shouldBe PersonligSimuleringResultV9(
            alderspensjon = listOf(
                PersonligSimuleringAlderspensjonResultV9(
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
            alderspensjonMaanedligVedEndring = PersonligSimuleringMaanedligPensjonResultV9(
                gradertUttakMaanedligBeloep = 6,
                heltUttakMaanedligBeloep = 7
            ),
            afpPrivat = listOf(
                PersonligSimuleringAfpPrivatResultV9(
                    alder = 67,
                    beloep = 14000,
                    kompensasjonstillegg = 123,
                    kronetillegg = 69,
                    livsvarig = 321,
                    maanedligBeloep = 1000
                ),
                PersonligSimuleringAfpPrivatResultV9(
                    alder = 68,
                    beloep = 12000,
                    kompensasjonstillegg = 123,
                    kronetillegg = 69,
                    livsvarig = 321,
                    maanedligBeloep = 1000
                ),
                PersonligSimuleringAfpPrivatResultV9(
                    alder = 69,
                    beloep = 13000,
                    kompensasjonstillegg = 123,
                    kronetillegg = 69,
                    livsvarig = 321,
                    maanedligBeloep = 1000
                )
            ),
            afpOffentlig = listOf(
                PersonligSimuleringAarligPensjonResultV9(
                    alder = 67,
                    beloep = 12000,
                    maanedligBeloep = 1000
                )
            ),
            vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV9(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = null,
            opptjeningGrunnlagListe = null
        )
    }

    context("listene over alderspensjon og privat AFP") {
        should("fjerne dagens alder hvis bruker fyller år senere i måneden") {
            val now = LocalDate.now()
            val foedselsdato = now.minusYears(63).plusMonths(1).withDayOfMonth(1).minusDays(1) //siste dag i måneden
            PersonligSimuleringResultMapperV9.resultV9(
                source = SimuleringResult(
                    alderspensjon = listOf(
                        alderspensjon(alder = 62, beloep = 1),
                        alderspensjon(alder = 63, beloep = 2),
                        alderspensjon(alder = 64, beloep = 3)
                    ),
                    alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                    afpPrivat = listOf(
                        privatAfp(alder = 62, beloep = 12000),
                        privatAfp(alder = 63, beloep = 12000),
                        privatAfp(alder = 64, beloep = 12000)
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
                foedselsdato = foedselsdato
            ) shouldBe PersonligSimuleringResultV9(
                alderspensjon = listOf(
                    PersonligSimuleringAlderspensjonResultV9(
                        alder = 62,
                        beloep = 1,
                        inntektspensjonBeloep = null,
                        garantipensjonBeloep = null,
                        delingstall = null,
                        pensjonBeholdningFoerUttakBeloep = null
                    ),
                    PersonligSimuleringAlderspensjonResultV9(
                        alder = 63,
                        beloep = 2,
                        inntektspensjonBeloep = null,
                        garantipensjonBeloep = null,
                        delingstall = null,
                        pensjonBeholdningFoerUttakBeloep = null
                    ),
                    PersonligSimuleringAlderspensjonResultV9(
                        alder = 64,
                        beloep = 3,
                        inntektspensjonBeloep = null,
                        garantipensjonBeloep = null,
                        delingstall = null,
                        pensjonBeholdningFoerUttakBeloep = null
                    ),
                ),
                alderspensjonMaanedligVedEndring = PersonligSimuleringMaanedligPensjonResultV9(
                    gradertUttakMaanedligBeloep = 6,
                    heltUttakMaanedligBeloep = 7
                ),
                afpPrivat = listOf(
                    PersonligSimuleringAfpPrivatResultV9(
                        alder = 62,
                        beloep = 12000,
                        kompensasjonstillegg = 123,
                        kronetillegg = 69,
                        livsvarig = 321,
                        maanedligBeloep = 1000
                    ),
                    PersonligSimuleringAfpPrivatResultV9(
                        alder = 63,
                        beloep = 12000,
                        kompensasjonstillegg = 123,
                        kronetillegg = 69,
                        livsvarig = 321,
                        maanedligBeloep = 1000
                    ),
                    PersonligSimuleringAfpPrivatResultV9(
                        alder = 64,
                        beloep = 12000,
                        kompensasjonstillegg = 123,
                        kronetillegg = 69,
                        livsvarig = 321,
                        maanedligBeloep = 1000
                    )
                ),
                afpOffentlig = listOf(
                    PersonligSimuleringAarligPensjonResultV9(
                        alder = 67,
                        beloep = 12000,
                        maanedligBeloep = 1000
                    )
                ),
                vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV9(
                    vilkaarErOppfylt = true,
                    alternativ = null
                ),
                harForLiteTrygdetid = true,
                trygdetid = null,
                opptjeningGrunnlagListe = null
            )
        }
    }
})

private fun alderspensjon(alder: Int, beloep: Int) =
    SimulertAlderspensjon(
        alder,
        beloep,
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
        skjermingstillegg = 14,
        kapittel19Gjenlevendetillegg = 15
    )

private fun privatAfp(alder: Int, beloep: Int) =
    SimulertAfpPrivat(
        alder,
        beloep,
        kompensasjonstillegg = 123,
        kronetillegg = 69,
        livsvarig = 321,
        maanedligBeloep = 1000
    )
