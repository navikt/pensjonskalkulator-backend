package no.nav.pensjon.kalkulator.simulering.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import org.junit.jupiter.api.Test

class SimuleringExtendedResultMapperV6Test {

    @Test
    fun `extendedResultV6 maps domain to V6 DTO`() {
        SimuleringExtendedResultMapperV6.extendedResultV6(
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
                afpPrivat = listOf(SimulertAfpPrivat(alder = 67, beloep = 12000)),
                afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 12000)),
                vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                harForLiteTrygdetid = true,
                trygdetid = 10,
                opptjeningGrunnlagListe = listOf(
                    SimulertOpptjeningGrunnlag(aar = 2001, pensjonsgivendeInntektBeloep = 501000),
                    SimulertOpptjeningGrunnlag(aar = 2002, pensjonsgivendeInntektBeloep = 502000)
                )
            )
        ) shouldBe SimuleringResultatV6(
            alderspensjon = listOf(
                AlderspensjonsberegningV6(
                    alder = 67,
                    beloep = 123456,
                    inntektspensjonBeloep = 1,
                    garantipensjonBeloep = 2,
                    delingstall = 3.4,
                    pensjonBeholdningFoerUttakBeloep = 5
                )
            ),
            afpPrivat = listOf(PensjonsberegningV6(alder = 67, beloep = 12000)),
            afpOffentlig = listOf(PensjonsberegningAfpOffentligV6(alder = 67, beloep = 12000)),
            vilkaarsproeving = VilkaarsproevingV6(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = 10,
            opptjeningGrunnlagListe = listOf(
                SimulertOpptjeningGrunnlagV6(aar = 2001, pensjonsgivendeInntektBeloep = 501000),
                SimulertOpptjeningGrunnlagV6(aar = 2002, pensjonsgivendeInntektBeloep = 502000)
            )
        )
    }
}
