package no.nav.pensjon.kalkulator.simulering.api.v1.acl.result

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.simulering.AlderspensjonMaanedsbeloep
import no.nav.pensjon.kalkulator.simulering.SimuleringResult
import no.nav.pensjon.kalkulator.simulering.SimulertAfpPrivat
import no.nav.pensjon.kalkulator.simulering.SimulertAlderspensjon
import no.nav.pensjon.kalkulator.simulering.SimulertOpptjeningGrunnlag
import no.nav.pensjon.kalkulator.simulering.Vilkaarsproeving

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
                alderspensjonListe = listOf(
                    expectedAlderspensjon(
                        gjenlevendetillegg = null, // not mapped
                        extension = null // not mapped
                    )
                ),
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
                            inntektspensjonBeloep = 2,
                            garantipensjonBeloep = 3,
                            delingstall = 4.4,
                            pensjonBeholdningFoerUttakBeloep = 5,
                            andelsbroekKap19 = 6.6,
                            andelsbroekKap20 = 7.7,
                            sluttpoengtall = 8.8,
                            trygdetidKap19 = 9,
                            trygdetidKap20 = 10,
                            poengaarFoer92 = 11,
                            poengaarEtter91 = 12,
                            forholdstall = 13.13,
                            grunnpensjon = 14,
                            tilleggspensjon = 15,
                            pensjonstillegg = 16,
                            skjermingstillegg = 17
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
    SimulertAlderspensjon(
        alder = 65,
        beloep = 1,
        inntektspensjonBeloep = 2,
        garantipensjonBeloep = 3,
        delingstall = 4.4,
        pensjonBeholdningFoerUttak = 5,
        andelsbroekKap19 = 6.6,
        andelsbroekKap20 = 7.7,
        sluttpoengtall = 8.8,
        trygdetidKap19 = 9,
        trygdetidKap20 = 10,
        poengaarFoer92 = 11,
        poengaarEtter91 = 12,
        forholdstall = 13.13,
        grunnpensjon = 14,
        tilleggspensjon = 15,
        pensjonstillegg = 16,
        skjermingstillegg = 17,
        kapittel19Gjenlevendetillegg = gjenlevendetillegg
    )

private fun expectedAlderspensjon(gjenlevendetillegg: Int?, extension: SimuleringV1AlderspensjonExtension?) =
    SimuleringV1Alderspensjon(
        alderAar = 65,
        beloep = 1,
        gjenlevendetillegg,
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
