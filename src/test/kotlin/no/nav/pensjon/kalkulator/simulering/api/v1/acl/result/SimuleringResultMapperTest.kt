package no.nav.pensjon.kalkulator.simulering.api.v1.acl.result

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.TestObjects
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.validity.Problem
import no.nav.pensjon.kalkulator.validity.ProblemType

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
                        gjenlevendetillegg = 500 // mapped
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

    context("problem") {
        should("map problemkode and -beskrivelse") {
            SimuleringResultMapper.toDto(
                source = SimuleringResult(
                    alderspensjon = emptyList(),
                    alderspensjonMaanedsbeloep = null,
                    pre2025OffentligAfp = null,
                    afpPrivat = emptyList(),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = vilkaarsproevingsresultat(innvilget = false),
                    harForLiteTrygdetid = false,
                    trygdetid = 0,
                    opptjeningGrunnlagListe = emptyList(),
                    problem = Problem(
                        type = ProblemType.UTILSTREKKELIG_INNTEKT,
                        beskrivelse = "10 kroner"
                    )
                ),
                naavaerendeAlderAar = 65,
                mode = MappingMode.INTERNAL
            ) shouldBe SimuleringV1Result(
                alderspensjonListe = emptyList(),
                maanedligAlderspensjonVedUttaksendring = null,
                livsvarigOffentligAfpListe = emptyList(),
                tidsbegrensetOffentligAfp = null,
                privatAfpListe = emptyList(),
                vilkaarsproevingsresultat = expectedVilkaarsproevingsresultat(innvilget = false),
                trygdetid = expectedTrygdetid(antallAar = 0),
                pensjonsgivendeInntektListe = emptyList(),
                problem = SimuleringV1Problem(
                    kode = SimuleringV1ProblemType.UTILSTREKKELIG_INNTEKT,
                    beskrivelse = "10 kroner"
                )
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
        inntektspensjonBeloep = null,
        basispensjonBeloep = null,
        garantipensjonBeloep = null,
        garantipensjonSats = null,
        garantitilleggBeloep = null,
        restpensjonBeloep = null,
        grunnpensjonBeloep = null,
        tilleggspensjonBeloep = null,
        pensjonstillegg = null,
        skjermingstillegg = null,
        gjenlevendetillegg = null,
        minstePensjonsnivaaSats = null,
        delingstall = null,
        forholdstall = null,
        pensjonsbeholdningFoerUttakBeloep = null,
        kapittel19Andel = null,
        kapittel20Andel = null,
        sluttpoengtall = null,
        kapittel19Trygdetid = null,
        kapittel20Trygdetid = null,
        poengaarTom1991 = null,
        poengaarFom1992 = null
    )

private fun expectedAlderspensjon(gjenlevendetillegg: Int?) =
    SimuleringV1Alderspensjon(
        alderAar = 65,
        beloep = 1,
        inntektspensjonBeloep = 1,
        basispensjonBeloep = 100,
        garantipensjonBeloep = 2,
        garantipensjonSats = 2.34,
        garantitilleggBeloep = 201,
        restpensjonBeloep = 101,
        grunnpensjonBeloep = 55810,
        tilleggspensjonBeloep = 134641,
        pensjonstillegg = -70243,
        skjermingstillegg = 14,
        gjenlevendetillegg = gjenlevendetillegg,
        minstePensjonsnivaaSats = 1.23,
        delingstall = 3.4,
        forholdstall = 0.971,
        pensjonsbeholdningFoerUttakBeloep = 5,
        kapittel19Andel = 0.6,
        kapittel20Andel = 0.4,
        sluttpoengtall = 5.11,
        kapittel19Trygdetid = 40,
        kapittel20Trygdetid = 39,
        poengaarTom1991 = 13,
        poengaarFom1992 = 27
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

private fun vilkaarsproevingsresultat(innvilget: Boolean = true) =
    Vilkaarsproeving(
        innvilget,
        alternativ = null
    )

private fun expectedVilkaarsproevingsresultat(innvilget: Boolean = true) =
    SimuleringV1Vilkaarsproevingsresultat(
        erInnvilget = innvilget,
        alternativ = null
    )

private fun expectedTrygdetid(antallAar: Int = 40) =
    SimuleringV1Trygdetid(
        antallAar,
        erUtilstrekkelig = false
    )
