package no.nav.pensjon.kalkulator.simulering.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SimuleringResultMapperV7Test {

    @Test
    fun `resultatV7 maps domain to V7 DTO`() {
        SimuleringResultMapperV7.resultatV7(
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
        ) shouldBe SimuleringResultatV7(
            alderspensjon = listOf(
                AlderspensjonsberegningV7(
                    alder = 67,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                )
            ),
            alderspensjonMaanedligVedEndring = AlderspensjonsMaanedligV7(
                gradertUttakMaanedligBeloep = 6,
                heltUttakMaanedligBeloep = 7
            ),
            afpPrivat = listOf(PensjonsberegningV7(alder = 67, beloep = 12000)),
            afpOffentlig = listOf(PensjonsberegningAfpOffentligV7(alder = 67, beloep = 12000)),
            vilkaarsproeving = VilkaarsproevingV7(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = null,
            opptjeningGrunnlagListe = null
        )
    }

    @Test
    fun `resultatV7 assigns 0 age to current age, replacing existing alderspensjon at current age, when mapping domain to V7 DTO`() {
        SimuleringResultMapperV7.resultatV7(
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
        ) shouldBe SimuleringResultatV7(
            alderspensjon = listOf(
                AlderspensjonsberegningV7(
                    alder = 67,
                    beloep = 1,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                ),
                AlderspensjonsberegningV7(
                    alder = 68,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                )
            ),
            alderspensjonMaanedligVedEndring = AlderspensjonsMaanedligV7(
                gradertUttakMaanedligBeloep = 6,
                heltUttakMaanedligBeloep = 7
            ),
            afpPrivat = listOf(PensjonsberegningV7(alder = 67, beloep = 12000)),
            afpOffentlig = listOf(PensjonsberegningAfpOffentligV7(alder = 67, beloep = 12000)),
            vilkaarsproeving = VilkaarsproevingV7(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = null,
            opptjeningGrunnlagListe = null
        )
    }

    @Test
    fun `resultatV7 assigns 0 age to current age and adds it to the list, when mapping domain to V7 DTO`() {
        SimuleringResultMapperV7.resultatV7(
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
        ) shouldBe SimuleringResultatV7(
            alderspensjon = listOf(
                AlderspensjonsberegningV7(
                    alder = 67,
                    beloep = 1,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                ),
                AlderspensjonsberegningV7(
                    alder = 68,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                ),
                AlderspensjonsberegningV7(
                    alder = 69,
                    beloep = 123456,
                    inntektspensjonBeloep = null,
                    garantipensjonBeloep = null,
                    delingstall = null,
                    pensjonBeholdningFoerUttakBeloep = null
                )
            ),
            alderspensjonMaanedligVedEndring = AlderspensjonsMaanedligV7(
                gradertUttakMaanedligBeloep = 6,
                heltUttakMaanedligBeloep = 7
            ),
            afpPrivat = listOf(PensjonsberegningV7(alder = 67, beloep = 12000)),
            afpOffentlig = listOf(PensjonsberegningAfpOffentligV7(alder = 67, beloep = 12000)),
            vilkaarsproeving = VilkaarsproevingV7(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = null,
            opptjeningGrunnlagListe = null
        )
    }
}
