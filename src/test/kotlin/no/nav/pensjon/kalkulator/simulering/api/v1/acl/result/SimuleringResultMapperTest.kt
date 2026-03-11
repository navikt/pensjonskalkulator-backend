package no.nav.pensjon.kalkulator.simulering.api.v1.acl.result

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.simulering.AlderspensjonMaanedsbeloep
import no.nav.pensjon.kalkulator.simulering.SimuleringResult
import no.nav.pensjon.kalkulator.simulering.SimulertAlderspensjon
import no.nav.pensjon.kalkulator.simulering.Vilkaarsproeving

class SimuleringResultMapperTest : ShouldSpec({

    context("normal mapping in external context") {
        should("map alderspensjon, not extension, not gjenlevenderett") {
            SimuleringResultMapper.toDto(
                source = SimuleringResult(
                    alderspensjon = listOf(
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
                            kapittel19Gjenlevendetillegg = 18
                        )
                    ),
                    alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(
                        gradertUttak = 100,
                        heltUttak = 101
                    ),
                    pre2025OffentligAfp = null,
                    afpPrivat = emptyList(),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = Vilkaarsproeving(
                        innvilget = true,
                        alternativ = null
                    ),
                    harForLiteTrygdetid = false,
                    trygdetid = 40,
                    opptjeningGrunnlagListe = emptyList(),
                    problem = null
                ),
                naavaerendeAlderAar = 65,
                mode = MappingMode.NORMAL_EXTERNAL
            ) shouldBe SimuleringV1Result(
                alderspensjonListe = listOf(
                    SimuleringV1Alderspensjon(
                        alderAar = 65,
                        beloep = 1,
                        gjenlevendetillegg = null,
                        extension = null
                    )
                ),
                maanedligAlderspensjonVedUttaksendring = SimuleringV1Uttaksbeloep(
                    gradertUttakMaanedligBeloep = 100,
                    heltUttakMaanedligBeloep = 101
                ),
                livsvarigOffentligAfpListe = emptyList(),
                tidsbegrensetOffentligAfp = null,
                privatAfpListe = emptyList(),
                vilkaarsproevingsresultat = SimuleringV1Vilkaarsproevingsresultat(
                    erInnvilget = true,
                    alternativ = null
                ),
                trygdetid = SimuleringV1Trygdetid(
                    antallAar = 40,
                    erUtilstrekkelig = false
                ),
                pensjonsgivendeInntektListe = emptyList(),
                problem = null
            )
        }
    }
})
