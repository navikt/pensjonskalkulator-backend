package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.*

class SimulatorPersonligSimuleringResultMapperTest : FunSpec({

    test("fromDto maps simulator-specific data transfer object to domain object") {
        SimulatorPersonligSimuleringResultMapper.fromDto(
            SimulatorPersonligSimuleringResult(
                alderspensjonListe = listOf(
                    pensjonDto(alderAar = 50, beloep = 1001),
                    pensjonDto(alderAar = 75, beloep = 1001)
                ),
                alderspensjonMaanedsbeloep = SimulatorPersonligMaanedsbeloep(
                    gradertUttakBeloep = null,
                    heltUttakBeloep = 0
                ),
                pre2025OffentligAfp = null,
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
                alderspensjon(alderAar = 50),
                alderspensjon(alderAar = 75)
            ),
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

    test("0 alder beholdes i listen") {
        SimulatorPersonligSimuleringResultMapper.fromDto(
            SimulatorPersonligSimuleringResult(
                alderspensjonListe = listOf(
                    pensjonDto(alderAar = 50, beloep = 1001),
                    pensjonDto(alderAar = 0, beloep = 1)
                ),
                alderspensjonMaanedsbeloep = null,
                pre2025OffentligAfp = null,
                privatAfpListe = emptyList(),
                livsvarigOffentligAfpListe = emptyList(),
                vilkaarsproeving = null,
                tilstrekkeligTrygdetidForGarantipensjon = false,
                trygdetid = 10,
                opptjeningGrunnlagListe = emptyList(),
                error = null
            )
        ).alderspensjon.first { it.alder == 0 }.beloep shouldBe 1
    }
})

private fun pensjonDto(alderAar: Int, beloep: Int) =
    SimulatorPersonligPensjon(
        alderAar,
        beloep,
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
        skjermingstillegg = 14,
        kapittel19Gjenlevendetillegg = 15
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
        skjermingstillegg = 14,
        kapittel19Gjenlevendetillegg = 15
    )
