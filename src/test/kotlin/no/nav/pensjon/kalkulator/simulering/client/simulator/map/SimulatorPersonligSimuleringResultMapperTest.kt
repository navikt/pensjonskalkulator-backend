package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.*
import org.junit.jupiter.api.Test

class SimulatorPersonligSimuleringResultMapperTest {

    @Test
    fun `fromDto maps simulator-specific data transfer object to domain object`() {
        SimulatorPersonligSimuleringResultMapper.fromDto(
            SimulatorPersonligSimuleringResult(
                alderspensjonListe = emptyList(),
                alderspensjonMaanedsbeloep = SimulatorPersonligMaanedsbeloep(
                    gradertUttakBeloep = null,
                    heltUttakBeloep = 0
                ),
                privatAfpListe = emptyList(),
                livsvarigOffentligAfpListe = emptyList(),
                vilkaarsproeving = SimulatorPersonligVilkaarsproeving(
                    vilkaarErOppfylt = false,
                    alternativ = SimulatorPersonligAlternativ(
                        gradertUttakAlder = null,
                        uttaksgrad = null,
                        heltUttakAlder = SimulatorPersonligAlder(aar = 65, maaneder = 4)
                    )
                ),
                tilstrekkeligTrygdetidForGarantipensjon = false,
                trygdetid = 10,
                opptjeningGrunnlagListe = listOf(
                    SimulatorPersonligOpptjeningGrunnlag(aar = 2001, pensjonsgivendeInntektBeloep = 123000)
                ),
                error = null
            )
        ) shouldBe SimuleringResult(
            alderspensjon = emptyList(),
            alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(
                gradertUttak = null,
                heltUttak = 0
            ),
            afpPrivat = emptyList(),
            afpOffentlig = emptyList(),
            vilkaarsproeving = Vilkaarsproeving(
                innvilget = false,
                alternativ = Alternativ(
                    gradertUttakAlder = null,
                    uttakGrad = null,
                    heltUttakAlder = Alder(aar = 65, maaneder = 4)
                )
            ),
            harForLiteTrygdetid = true,
            trygdetid = 10,
            opptjeningGrunnlagListe = listOf(
                SimulertOpptjeningGrunnlag(aar = 2001, pensjonsgivendeInntektBeloep = 123000)
            )
        )
    }

    @Test
    fun `0 alder beholdes i listen`() {
        SimulatorPersonligSimuleringResultMapper.fromDto(
            SimulatorPersonligSimuleringResult(
                alderspensjonListe = listOf(
                    pensjonDto(alderAar = 100),
                    pensjonDto(alderAar = 50),
                    pensjonDto(alderAar = 75),
                    SimulatorPersonligPensjon(
                        alderAar = 0,
                        beloep = 1,
                        inntektspensjon = 2,
                        garantipensjon = 3,
                        delingstall = 0.4,
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
                alderspensjonMaanedsbeloep = SimulatorPersonligMaanedsbeloep(
                    gradertUttakBeloep = null,
                    heltUttakBeloep = 0
                ),
                privatAfpListe = emptyList(),
                livsvarigOffentligAfpListe = emptyList(),
                vilkaarsproeving = SimulatorPersonligVilkaarsproeving(
                    vilkaarErOppfylt = false,
                    alternativ = SimulatorPersonligAlternativ(
                        gradertUttakAlder = null,
                        uttaksgrad = null,
                        heltUttakAlder = SimulatorPersonligAlder(aar = 65, maaneder = 4)
                    )
                ),
                tilstrekkeligTrygdetidForGarantipensjon = false,
                trygdetid = 10,
                opptjeningGrunnlagListe = listOf(
                    SimulatorPersonligOpptjeningGrunnlag(aar = 2001, pensjonsgivendeInntektBeloep = 123000)
                ),
                error = null
            )
        ) shouldBe SimuleringResult(
            alderspensjon = listOf(
                alderspensjon(alderAar = 100),
                alderspensjon(alderAar = 50),
                alderspensjon(alderAar = 75),
                SimulertAlderspensjon(
                    alder = 0,
                    beloep = 1,
                    inntektspensjonBeloep = 2,
                    garantipensjonBeloep = 3,
                    delingstall = 0.4,
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
            alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = null, heltUttak = 0),
            afpPrivat = emptyList(),
            afpOffentlig = emptyList(),
            vilkaarsproeving = Vilkaarsproeving(
                innvilget = false,
                alternativ = Alternativ(
                    gradertUttakAlder = null,
                    uttakGrad = null,
                    heltUttakAlder = Alder(aar = 65, maaneder = 4)
                )
            ),
            harForLiteTrygdetid = true,
            trygdetid = 10,
            opptjeningGrunnlagListe = listOf(
                SimulertOpptjeningGrunnlag(aar = 2001, pensjonsgivendeInntektBeloep = 123000)
            )
        )
    }

    private companion object {
        private fun pensjonDto(alderAar: Int) =
            SimulatorPersonligPensjon(
                alderAar,
                beloep = 1001,
                inntektspensjon = 2001,
                garantipensjon = 3001,
                delingstall = 0.51,
                pensjonBeholdningFoerUttak = 4001,
                andelsbroekKap19 = 0.6,
                andelsbroekKap20 = 0.4,
                sluttpoengtall = 5.11,
                trygdetidKap19 = 40,
                trygdetidKap20 = 40,
                poengaarFoer92 = 13,
                poengaarEtter91 = 27,
                forholdstall = 0.971,
                grunnpensjon = 50810,
                tilleggspensjon = 134641,
                pensjonstillegg = 61243,
                skjermingstillegg = 0
            )

        private fun alderspensjon(alderAar: Int) =
            SimulertAlderspensjon(
                alderAar,
                beloep = 1001,
                inntektspensjonBeloep = 2001,
                garantipensjonBeloep = 3001,
                delingstall = 0.51,
                pensjonBeholdningFoerUttak = 4001,
                andelsbroekKap19 = 0.6,
                andelsbroekKap20 = 0.4,
                sluttpoengtall = 5.11,
                trygdetidKap19 = 40,
                trygdetidKap20 = 40,
                poengaarFoer92 = 13,
                poengaarEtter91 = 27,
                forholdstall = 0.971,
                grunnpensjon = 50810,
                tilleggspensjon = 134641,
                pensjonstillegg = 61243,
                skjermingstillegg = 0
            )
    }
}
