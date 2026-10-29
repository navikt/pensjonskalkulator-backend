package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.merknad.Merknad
import no.nav.pensjon.kalkulator.merknad.MerknadCode
import no.nav.pensjon.kalkulator.merknad.UttaksgradMerknad
import no.nav.pensjon.kalkulator.mock.TestObjects
import no.nav.pensjon.kalkulator.simulering.AlderspensjonMaanedsbeloep
import no.nav.pensjon.kalkulator.simulering.SimuleringResult
import no.nav.pensjon.kalkulator.simulering.SimulertAfpPrivat
import no.nav.pensjon.kalkulator.simulering.SimulertOpptjening
import no.nav.pensjon.kalkulator.simulering.Vilkaarsproeving

class SimuleringV2ResultTest : ShouldSpec({

    should("mappe fra domenerepresentasjon til overførbar representasjon (DTO)") {
        SimuleringV2Result.transferable(
            source = SimuleringResult(
                alderspensjonListe = listOf(alderspensjon()),
                alderspensjonMaanedsbeloep = uttaksbeloep(),
                livsvarigOffentligAfpListe = emptyList(),
                tidsbegrensetOffentligAfp = null,
                privatAfpListe = listOf(
                    SimulertAfpPrivat(
                        alder = 64,
                        beloep = 200,
                        kompensasjonstillegg = 201,
                        kronetillegg = 202,
                        livsvarig = 203,
                        maanedligBeloep = 204
                    )
                ),
                vilkaarsproeving = vilkaarsproevingsresultat(),
                harForLiteTrygdetid = false,
                trygdetid = 40,
                opptjeningListe = listOf(
                    SimulertOpptjening(
                        aarstall = 2001,
                        pensjonsgivendeInntektBeloep = 1,
                        pensjonspoeng = 2.1,
                        pensjonsbeholdningBeloep = 3,
                        merknadListe = listOf(
                            Merknad(kode = MerknadCode.OMSORGSOPPTJENING),
                            Merknad(
                                kode = MerknadCode.GRADERT_UTTAK,
                                uttaksgrad = UttaksgradMerknad(
                                    grad = Uttaksgrad.FEMTI_PROSENT,
                                    kalendermaanedFom = 6
                                )
                            )
                        )
                    )
                ),
                problem = null
            ),
            naavaerendeAlderAar = 65
        ) shouldBe SimuleringV2Result(
            alderspensjonListe = listOf(expectedAlderspensjon()),
            maanedligAlderspensjonVedUttaksendring = expectedUttaksbeloep(),
            maanedligAlderspensjonForKnekkpunkter = null,
            livsvarigOffentligAfpListe = emptyList(),
            tidsbegrensetOffentligAfp = null,
            serviceberegnetAfp = null,
            privatAfpListe = listOf(
                SimuleringV2PrivatAfp(
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
            opptjeningListe = listOf(
                SimuleringV2Opptjening(
                    aarstall = 2001,
                    pensjonsgivendeInntektBeloep = 1,
                    pensjonspoeng = 2.1,
                    pensjonsbeholdningBeloep = 3,
                    merknadListe = listOf(
                        SimuleringV2Merknad(kode = SimuleringV2Merknadkode.OMSORGSOPPTJENING),
                        SimuleringV2Merknad(
                            kode = SimuleringV2Merknadkode.GRADERT_UTTAK,
                            uttaksgrad = SimuleringV2Uttaksgrad(
                                grad = 50,
                                kalendermaanedFom = 6
                            )
                        )
                    )
                )
            ),
            problem = null
        )
    }
})

private fun alderspensjon() =
    TestObjects.alderspensjon(gjenlevendetillegg = 500, alderAar = 65, beloep = 1)

private fun expectedAlderspensjon() =
    SimuleringV2Alderspensjon(
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
        gjenlevendetillegg = 500,
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
    SimuleringV2Uttaksbeloep(
        gradertUttakMaanedligBeloep = 100,
        heltUttakMaanedligBeloep = 101
    )

private fun vilkaarsproevingsresultat(innvilget: Boolean = true) =
    Vilkaarsproeving(
        innvilget,
        alternativ = null
    )

private fun expectedVilkaarsproevingsresultat(innvilget: Boolean = true) =
    SimuleringV2Vilkaarsproevingsresultat(
        erInnvilget = innvilget,
        alternativ = null
    )

private fun expectedTrygdetid(antallAar: Int = 40) =
    SimuleringV2Trygdetid(
        antallAar,
        erUtilstrekkelig = false
    )