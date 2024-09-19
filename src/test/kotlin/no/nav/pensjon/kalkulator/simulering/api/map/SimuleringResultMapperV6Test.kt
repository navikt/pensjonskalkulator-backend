package no.nav.pensjon.kalkulator.simulering.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.simulering.SimuleringResult
import no.nav.pensjon.kalkulator.simulering.SimulertAlderspensjon
import no.nav.pensjon.kalkulator.simulering.SimulertOpptjeningGrunnlag
import no.nav.pensjon.kalkulator.simulering.Vilkaarsproeving
import no.nav.pensjon.kalkulator.simulering.api.dto.AlderspensjonsberegningV6
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringResultatV6
import no.nav.pensjon.kalkulator.simulering.api.dto.SimulertOpptjeningGrunnlagV6
import no.nav.pensjon.kalkulator.simulering.api.dto.VilkaarsproevingV6
import org.junit.jupiter.api.Test

class SimuleringResultMapperV6Test {

    @Test
    fun `resultatV6 maps domain to V6 DTO`() {
        SimuleringResultMapperV6.resultatV6(
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
                afpPrivat = emptyList(),
                afpOffentlig = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                harForLiteTrygdetid = false,
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
            afpPrivat = emptyList(),
            afpOffentlig = emptyList(),
            vilkaarsproeving = VilkaarsproevingV6(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = false,
            opptjeningGrunnlagListe = listOf(
                SimulertOpptjeningGrunnlagV6(aar = 2001, pensjonsgivendeInntektBeloep = 501000),
                SimulertOpptjeningGrunnlagV6(aar = 2002, pensjonsgivendeInntektBeloep = 502000)
            )
        )
    }
}
