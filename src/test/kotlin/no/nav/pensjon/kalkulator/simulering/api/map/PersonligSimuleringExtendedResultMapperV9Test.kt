package no.nav.pensjon.kalkulator.simulering.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import java.time.LocalDate

class PersonligSimuleringExtendedResultMapperV9Test : ShouldSpec({

    should("map domain to V9 DTO") {
        PersonligSimuleringExtendedResultMapperV9.extendedResultV9(
            source = SimuleringResult(
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
                        skjermingstillegg = 14,
                        kapittel19Gjenlevendetillegg = 15
                    )
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(
                    SimulertAfpPrivat(
                        alder = 67,
                        beloep = 12000,
                        kompensasjonstillegg = 123,
                        kronetillegg = 69,
                        livsvarig = 321,
                        maanedligBeloep = 1000
                    )
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
            foedselsdato = LocalDate.of(1963, 1, 1)
        ) shouldBe PersonligSimuleringResultV9(
            alderspensjon = listOf(
                PersonligSimuleringAlderspensjonResultV9(
                    alder = 67,
                    beloep = 123456,
                    inntektspensjonBeloep = 1,
                    garantipensjonBeloep = 2,
                    delingstall = 3.4,
                    pensjonBeholdningFoerUttakBeloep = 5,
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
            trygdetid = 10,
            opptjeningGrunnlagListe = listOf(
                PersonligSimuleringAarligInntektResultV9(aar = 2001, pensjonsgivendeInntektBeloep = 501000),
                PersonligSimuleringAarligInntektResultV9(aar = 2002, pensjonsgivendeInntektBeloep = 502000)
            )
        )
    }
})
