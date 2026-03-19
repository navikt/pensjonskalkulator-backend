package no.nav.pensjon.kalkulator.simulering.api.v1.acl.result

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.TestObjects
import no.nav.pensjon.kalkulator.simulering.*

class SimuleringResultMapperTest : ShouldSpec({

    context("internal mapping mode") {
        should("map alderspensjon and gjenlevendetillegg, not extension") {
            SimuleringResultMapper.toDto(
                source = SimuleringResult(
                    alderspensjon = listOf(alderspensjon(gjenlevendetillegg = 500)),
                    alderspensjonMaanedsbeloep = uttaksbeloep(),
                    pre2025OffentligAfp = null,
                    afpPrivat = listOf(
                        SimulertAfpPrivat(
                            alder = 64,
                            beloep = 200,
                            kompensasjonstillegg = 201,
                            kronetillegg = 202,
                            livsvarig = 203,
                            maanedligBeloep = 204
                        )
                    ),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = vilkaarsproevingsresultat(),
                    harForLiteTrygdetid = false,
                    trygdetid = 40,
                    opptjeningGrunnlagListe = emptyList(),
                    problem = null
                ),
                naavaerendeAlderAar = 65,
                mode = MappingMode.INTERNAL
            ) shouldBe SimuleringV1Result(
                alderspensjonListe = listOf(
                    expectedAlderspensjon(
                        gjenlevendetillegg = 500, // mapped
                        extension = null // not mapped
                    )
                ),
                maanedligAlderspensjonVedUttaksendring = expectedUttaksbeloep(),
                livsvarigOffentligAfpListe = emptyList(),
                tidsbegrensetOffentligAfp = null,
                privatAfpListe = listOf(
                    SimuleringV1PrivatAfp(
                        alderAar = 64,
                        aarligBeloep = 200,
                        kompensasjonstillegg = 201,
                        kronetillegg = 202,
                        livsvarig = 203,
                        maanedligBeloep = 204
                    )
                ),
                vilkaarsproevingsresultat = expectedVilkaarsproevingsresultat(),
                trygdetid = expectedTrygdetid(),
                pensjonsgivendeInntektListe = emptyList(),
                problem = null
            )
        }
    }

    context("normal external mapping mode") {
        should("map alderspensjon, not extension, not gjenlevendetillegg") {
            SimuleringResultMapper.toDto(
                source = SimuleringResult(
                    alderspensjon = listOf(alderspensjon(gjenlevendetillegg = 600)),
                    alderspensjonMaanedsbeloep = uttaksbeloep(),
                    pre2025OffentligAfp = null,
                    afpPrivat = emptyList(),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = vilkaarsproevingsresultat(),
                    harForLiteTrygdetid = false,
                    trygdetid = 40,
                    opptjeningGrunnlagListe = emptyList(),
                    problem = null
                ),
                naavaerendeAlderAar = 65,
                mode = MappingMode.NORMAL_EXTERNAL
            ) shouldBe SimuleringV1Result(
                alderspensjonListe = listOf(expectedAlderspensjonForReducedMapping()),
                maanedligAlderspensjonVedUttaksendring = expectedUttaksbeloep(),
                livsvarigOffentligAfpListe = emptyList(),
                tidsbegrensetOffentligAfp = null,
                privatAfpListe = emptyList(),
                vilkaarsproevingsresultat = expectedVilkaarsproevingsresultat(),
                trygdetid = expectedTrygdetid(),
                pensjonsgivendeInntektListe = emptyList(),
                problem = null
            )
        }
    }

    context("extended external mapping mode") {
        should("map alderspensjon, extension and gjenlevendetillegg") {
            SimuleringResultMapper.toDto(
                source = SimuleringResult(
                    alderspensjon = listOf(alderspensjon(gjenlevendetillegg = 700)),
                    alderspensjonMaanedsbeloep = uttaksbeloep(),
                    pre2025OffentligAfp = null,
                    afpPrivat = emptyList(),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = vilkaarsproevingsresultat(),
                    harForLiteTrygdetid = false,
                    trygdetid = 40,
                    opptjeningGrunnlagListe = listOf(
                        SimulertOpptjeningGrunnlag(aar = 2021, pensjonsgivendeInntektBeloep = 10000)
                    ),
                    problem = null
                ),
                naavaerendeAlderAar = 65,
                mode = MappingMode.EXTENDED_EXTERNAL
            ) shouldBe SimuleringV1Result(
                alderspensjonListe = listOf(
                    expectedAlderspensjon(
                        gjenlevendetillegg = 700, // mapped
                        extension = SimuleringV1AlderspensjonExtension( // mapped
                            inntektspensjonBeloep = 1,
                            delingstall = 3.4,
                            pensjonBeholdningFoerUttakBeloep = 5,
                            andelsbroekKap19 = 0.6,
                            andelsbroekKap20 = 0.4,
                            sluttpoengtall = 5.11,
                            trygdetidKap19 = 40,
                            trygdetidKap20 = 39,
                            poengaarFoer92 = 13,
                            poengaarEtter91 = 27,
                            forholdstall = 0.971,
                            grunnpensjon = 55810,
                            tilleggspensjon = 134641,
                            pensjonstillegg = -70243,
                            skjermingstillegg = 14
                        )
                    )
                ),
                maanedligAlderspensjonVedUttaksendring = expectedUttaksbeloep(),
                livsvarigOffentligAfpListe = emptyList(),
                tidsbegrensetOffentligAfp = null,
                privatAfpListe = emptyList(),
                vilkaarsproevingsresultat = expectedVilkaarsproevingsresultat(),
                trygdetid = expectedTrygdetid(),
                pensjonsgivendeInntektListe = listOf(
                    SimuleringV1AarligBeloep(aarstall = 2021, beloep = 10000)
                ),
                problem = null
            )
        }
    }
})

private fun alderspensjon(gjenlevendetillegg: Int) =
    TestObjects.alderspensjon(gjenlevendetillegg = gjenlevendetillegg, alderAar = 65, beloep = 1)

private fun expectedAlderspensjonForReducedMapping() =
    SimuleringV1Alderspensjon(
        alderAar = 65,
        beloep = 1,
        basispensjonBeloep = null,
        garantipensjonBeloep = null,
        garantipensjonSats = null,
        garantitilleggBeloep = null,
        restpensjonBeloep = null,
        gjenlevendetillegg = null,
        minstePensjonsnivaaSats = null,
        extension = null
    )

private fun expectedAlderspensjon(gjenlevendetillegg: Int?, extension: SimuleringV1AlderspensjonExtension?) =
    SimuleringV1Alderspensjon(
        alderAar = 65,
        beloep = 1,
        basispensjonBeloep = 100,
        garantipensjonBeloep = 2,
        garantipensjonSats = 2.34,
        garantitilleggBeloep = 201,
        restpensjonBeloep = 101,
        gjenlevendetillegg,
        minstePensjonsnivaaSats = 1.23,
        extension
    )

private fun uttaksbeloep() =
    AlderspensjonMaanedsbeloep(
        gradertUttak = 100,
        heltUttak = 101
    )

private fun expectedUttaksbeloep() =
    SimuleringV1Uttaksbeloep(
        gradertUttakMaanedligBeloep = 100,
        heltUttakMaanedligBeloep = 101
    )

private fun vilkaarsproevingsresultat() =
    Vilkaarsproeving(
        innvilget = true,
        alternativ = null
    )

private fun expectedVilkaarsproevingsresultat() =
    SimuleringV1Vilkaarsproevingsresultat(
        erInnvilget = true,
        alternativ = null
    )

private fun expectedTrygdetid() =
    SimuleringV1Trygdetid(
        antallAar = 40,
        erUtilstrekkelig = false
    )
